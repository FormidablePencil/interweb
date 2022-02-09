package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CompositionCarousel
import dtos.compositions.carousels.CompositionCarousel.*

class CarouselsManager(
    private val carouselOfImagesManager: CarouselOfImagesManager,
    private val carouselBlurredOverlayManager: CarouselBlurredOverlayManager,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) : ICompositionCategoryManagerStructure<CompositionCarousel, CarouselBasicImagesRes, CompositionResponse> {
    private val gson = Gson()

    override fun getPrivateComposition(
        compositionType: CompositionCarousel,
        compositionSourceId: Int,
        authorId: Int
    ): CarouselBasicImagesRes? = when (compositionType) {
        CarouselBlurredOverlay ->
            carouselOfImagesManager.getPrivateComposition(compositionSourceId, authorId)
        CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
        BasicImages ->
            carouselOfImagesManager.getPrivateComposition(compositionSourceId, authorId)
    }

    override fun createCompositionOfCategory(
        compositionType: CompositionCarousel,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): CompositionResponse = when (compositionType) {
        CarouselBlurredOverlay ->
            carouselBlurredOverlayManager.createComposition(
                gson.fromJson(jsonData, CreateCarouselBasicImagesReq::class.java), layoutId,
                userId
            )
        CarouselMagnifying -> TODO() // todo - this is a style variant of BasicImages
        BasicImages ->
            carouselOfImagesManager.createComposition(
                gson.fromJson(jsonData, CreateCarouselBasicImagesReq::class.java), layoutId,
                userId
            )
    }

    override fun updateComposition(
        compositionType: CompositionCarousel,
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
        TODO("Not yet implemented")
    }

    override fun deleteComposition(
        compositionType: CompositionCarousel,
        compositionSourceId: Int,
        authorId: Int
    ): Boolean = when (compositionType) {
        CarouselBlurredOverlay ->
            carouselOfImagesManager.deleteComposition(compositionSourceId, authorId)
        CarouselMagnifying -> TODO()
        BasicImages ->
            carouselOfImagesManager.deleteComposition(compositionSourceId, authorId)
    }
//    TODO("Not yet implemented")
}