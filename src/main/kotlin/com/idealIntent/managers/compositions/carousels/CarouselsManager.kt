package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.carousels.CompositionCarouselType
import dtos.compositions.carousels.CompositionCarouselType.*

class CarouselsManager(
    private val carouselOfImagesManager: CarouselOfImagesManager,
    private val carouselBlurredOverlayManager: CarouselBlurredOverlayManager,
) : ICompositionCategoryManagerStructure<CompositionCarouselType, CarouselBasicImagesRes, CompositionResponse> {
    private val gson = Gson()

    override fun getPublicComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int,
    ): CarouselBasicImagesRes? = when (compositionType) {
        CarouselBlurredOverlay ->
            carouselBlurredOverlayManager.getPublicComposition(compositionSourceId)
        CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
        BasicImages ->
            carouselOfImagesManager.getPublicComposition(compositionSourceId)
    }

    override fun getPrivateComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int,
        authorId: Int
    ): CarouselBasicImagesRes? = when (compositionType) {
        CarouselBlurredOverlay ->
            carouselBlurredOverlayManager.getPrivateComposition(compositionSourceId, authorId)
        CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
        BasicImages ->
            carouselOfImagesManager.getPrivateComposition(compositionSourceId, authorId)
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
                    gson.fromJson(jsonData, CreateCarouselBasicImagesReq::class.java),
                    layoutId, authorId
                )
            CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
            BasicImages ->
                carouselOfImagesManager.createComposition(
                    gson.fromJson(jsonData, CreateCarouselBasicImagesReq::class.java),
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