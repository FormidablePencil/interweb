package com.idealIntent.managers.compositions

import com.google.gson.Gson
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.BannerBasic
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImages
import dtos.space.IUserComposition

// todo - inserts, update and deletes in each composition manager
class CarouselManager(
    private val bannerRepository: BasicBannerRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) {

    fun insertComposition(request: IUserComposition, spaceAddress: String): Boolean {
        val gson = Gson()
        return when (request.compositionType) {
            // region Carousels
            CompositionCategory.Carousel -> {
                // todo - accesses carouselCompositions.composition directly
                return carouselOfImagesRepository.insertComposition(
                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java)
                ) != null
            }
            // endregion

            // region Just texts
            CompositionCategory.Markdown -> TODO()
            CompositionCategory.Text -> TODO()
            // endregion

            // region Banners
            CompositionCategory.Banner ->
                bannerRepository.createBannerBasic(
                    gson.fromJson(request.jsonData, BannerBasic::class.java)
                ) != null
            // endregion

            // region Grids
            CompositionCategory.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionCategory.Divider -> TODO()
            CompositionCategory.LineDivider -> TODO()
            // endregion
        }
    }
}