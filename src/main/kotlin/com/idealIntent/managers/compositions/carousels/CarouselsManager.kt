package com.idealIntent.managers.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CarouselBlurredOverlayCreateReq
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesCreateReq
import com.idealIntent.managers.compositions.CompositionCategoryManagerStructure
import dtos.compositions.carousels.CompositionCarouselType
import dtos.compositions.carousels.CompositionCarouselType.*

class CarouselsManager(
    private val carouselOfImagesManager: CarouselOfImagesManager,
    private val carouselBlurredOverlayManager: CarouselBlurredOverlayManager,
) : CompositionCategoryManagerStructure<CompositionCarouselType, CarouselBasicImagesRes, CompositionResponse>() {

    override fun getPublicComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int,
    ): Pair<CompositionCarouselType, String> = when (compositionType) {
        CarouselBlurredOverlay ->
            Pair(
                CarouselBlurredOverlay,
                gson.toJson(carouselBlurredOverlayManager.getPublicComposition(compositionSourceId))
            )
        CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
        BasicImages ->
            Pair(
                CarouselBlurredOverlay,
                gson.toJson(carouselOfImagesManager.getPublicComposition(compositionSourceId))
            )
    }

    override fun getPrivateComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int,
        authorId: Int
    ): Pair<CompositionCarouselType, String> = when (compositionType) {
        CarouselBlurredOverlay ->
            Pair(
                CarouselBlurredOverlay,
                gson.toJson(carouselBlurredOverlayManager.getPrivateComposition(compositionSourceId, authorId))
            )

        CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
        BasicImages ->
            Pair(
                CarouselBlurredOverlay,
                gson.toJson(carouselOfImagesManager.getPrivateComposition(compositionSourceId, authorId))
            )
    }

    override fun createComposition(
        compositionType: CompositionCarouselType,
        jsonData: String,
        layoutId: Int,
        authorId: Int
    ): Int {
        return when (compositionType) {
            CarouselBlurredOverlay ->
                carouselBlurredOverlayManager.createComposition(
                    gson.fromJson(jsonData, CarouselBlurredOverlayCreateReq::class.java),
                    layoutId, authorId
                )
            CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
            BasicImages ->
                carouselOfImagesManager.createComposition(
                    gson.fromJson(jsonData, CarouselBasicImagesCreateReq::class.java),
                    layoutId, authorId
                )
        }
    }

    override fun updateComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int,
    ) {
        when (compositionType) {
            CarouselBlurredOverlay ->
                carouselBlurredOverlayManager.updateComposition(
                    compositionSourceId = compositionSourceId,
                    compositionUpdateQue = compositionUpdateQue,
                    authorId = authorId,
                )
            CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
            BasicImages ->
                carouselOfImagesManager.updateComposition(
                    compositionSourceId = compositionSourceId,
                    compositionUpdateQue = compositionUpdateQue,
                    authorId = authorId,
                )
        }
    }

    override fun deleteComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int,
        authorId: Int
    ) = when (compositionType) {
        CarouselBlurredOverlay ->
            carouselBlurredOverlayManager.deleteComposition(compositionSourceId, authorId)
        CarouselMagnifying -> TODO()
        BasicImages ->
            carouselOfImagesManager.deleteComposition(compositionSourceId, authorId)
    }
}