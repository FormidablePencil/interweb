package com.idealIntent.managers

import com.google.gson.Gson
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.carousels.CompositionCarousel

// todo - comments
// todo - move this logic to services
class CompositionManager(
    private val spaceRepository: SpaceRepository,
    private val bannerRepository: BasicBannerRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
    private val imageRepository: ImageRepository,
    private val textRepository: TextRepository,
    private val privilegeRepository: PrivilegeRepository,
) {
    fun getComposition() {
        // given some identifiers
        // get data of CompositionCategory by identifier
    }

    fun getCompositionOfSpace(spaceAddress: String) {

    }

    fun insertComposition(carouselType: Int): Int? {
        val gson = Gson()
        return when (CompositionCarousel.fromInt(carouselType)) {
            CompositionCarousel.CarouselBlurredOverlay -> TODO()
            CompositionCarousel.CarouselMagnifying -> TODO()
            CompositionCarousel.BasicImages ->
                carouselOfImagesRepository.insertComposition(
                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java),
                )
        }

        // todo - Response object
    }