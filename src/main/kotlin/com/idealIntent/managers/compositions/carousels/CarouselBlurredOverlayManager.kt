package com.idealIntent.managers.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.*
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.CompositionTypeManagerStructure
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.carousels.CarouselBlurredOverlayRepository
import dtos.compositions.carousels.CompositionCarouselType

class CarouselBlurredOverlayManager(
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionSourceRepository: CompositionSourceRepository,
    private val carouselBlurredOverlayRepository: CarouselBlurredOverlayRepository,
    private val spaceRepository: SpaceRepository
) : CompositionTypeManagerStructure<CarouselBlurredOverlayRes, IImagesCarouselEntity,
        CarouselBlurredOverlayCreateReq, CarouselOfImagesComposePrepared, CompositionResponse>() {

    override fun getPublicComposition(compositionSourceId: Int) =
        carouselBlurredOverlayRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int) =
        carouselBlurredOverlayRepository.getPrivateComposition(compositionSourceId, authorId)

    override fun createComposition(createRequest: CarouselBlurredOverlayCreateReq, layoutId: Int, authorId: Int): Int {
        appEnv.database.useTransaction {
            val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
            val textCollectionId = textRepository.batchInsertRecordsToNewCollection(createRequest.texts)

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

            carouselBlurredOverlayRepository.compose(
                CarouselBlurredOverlayComposePrepared(
                    imageCollectionId = imageCollectionId,
                    textCollectionId = textCollectionId,
                ),
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