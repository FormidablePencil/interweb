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
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) : ICompositionCategoryManagerStructure<CarouselBasicImagesRes, CompositionResponse> {
    private val gson = Gson()

    override fun getComposition(compositionType: CompositionCarousel, compositionId: Int, authorId: Int): CarouselBasicImagesRes? =
        when (compositionType) {
            CarouselBlurredOverlay -> TODO()
            CarouselMagnifying -> TODO()
            BasicImages ->
                carouselOfImagesRepository.getSingleCompositionOfPrivilegedAuthor(compositionId, authorId).first()
        }

    override fun createCompositionOfCategory(compositionType: CompositionCarousel, jsonData: String, userId: Int): CompositionResponse =
        when (compositionType) {
            CarouselBlurredOverlay -> TODO()
            CarouselMagnifying -> TODO()
            BasicImages ->
                carouselOfImagesManager.createComposition(gson.fromJson(jsonData, CreateCarouselBasicImagesReq::class.java), userId)
        }

    override fun updateComposition() {
        TODO("Not yet implemented")
    }

    override fun deleteComposition() {
        TODO("Not yet implemented")
    }
}