package com.idealIntent.managers.compositions.headers

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.CompositionTypeManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.headers.HeaderBasicRepository
import dtos.compositions.carousels.CompositionCarouselType

class HeaderBasicManager(
    private val headerBasicRepository: HeaderBasicRepository,
    private val spaceRepository: SpaceRepository,
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
) : CompositionTypeManagerStructure<HeaderBasicRes, IImagesCarouselEntity, HeaderBasicCreateReq,
        CarouselOfImagesComposePrepared, CompositionResponse>() {

    override fun getPublicComposition(compositionSourceId: Int): HeaderBasicRes? =
        headerBasicRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): HeaderBasicRes? =
        headerBasicRepository.getPrivateComposition(compositionSourceId, authorId)

    override fun createComposition(createRequest: HeaderBasicCreateReq, layoutId: Int, authorId: Int): Int {
        appEnv.database.useTransaction {
            val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
                compositionType = CompositionCarouselType.BasicImages.value,
                privilegeLevel = createRequest.privilegeLevel,
                name = createRequest.name,
                authorId = authorId,
            )

            spaceRepository.associateCompositionToLayout(
                orderRank = 0,
                compositionSourceId = compositionSourceId,
                layoutId = layoutId
            )

            headerBasicRepository.compose(
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