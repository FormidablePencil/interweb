package com.idealIntent.managers.compositions.banners

import com.idealIntent.dtos.compositions.banners.BannerBasicCreateReq
import com.idealIntent.dtos.compositions.banners.BannerBasicRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.CompositionTypeManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.banners.BannerImageRepository
import dtos.compositions.banners.CompositionBanner

class BannerImageManager(
    private val bannerImageRepository: BannerImageRepository,
    private val spaceRepository: SpaceRepository,
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
) : CompositionTypeManagerStructure<BannerBasicRes, IImagesCarouselEntity,
        BannerBasicCreateReq, BannerBasicRes, CompositionResponse>() {
    override fun getPublicComposition(compositionSourceId: Int): BannerBasicRes? =
        bannerImageRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): BannerBasicRes? =
        bannerImageRepository.getPrivateComposition(compositionSourceId, authorId)

    override fun createComposition(createRequest: BannerBasicCreateReq, layoutId: Int, authorId: Int): Int {
        appEnv.database.useTransaction {
            val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
                compositionType = CompositionBanner.Basic.value,
                privilegeLevel = createRequest.privilegeLevel,
                name = createRequest.name,
                authorId = authorId,
            )

            spaceRepository.associateCompositionToLayout(
                orderRank = 0,
                compositionSourceId = compositionSourceId,
                layoutId = layoutId
            )

            bannerImageRepository.compose(
                composePrepared = createRequest,
                sourceId = compositionSourceId
            )

            compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                createRequest.privilegedAuthors, compositionSourceId, authorId
            )

            return compositionSourceId
        }
    }

    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        TODO("Not yet implemented")
    }

}