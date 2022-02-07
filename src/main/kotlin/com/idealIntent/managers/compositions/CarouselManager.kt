package com.idealIntent.managers.compositions

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import dtos.compositions.CompositionCategory
import dtos.space.IUserComposition

// todo - inserts, update and deletes in each composition manager
class CarouselManager(
    private val bannerRepository: BasicBannerRepository,
    private val carouselOfImagesManager: CarouselOfImagesManager,
) {

    fun insertComposition(request: IUserComposition, layoutId: Int, userId: Int): CompositionResponse {
        val gson = Gson()
        return when (request.compositionType) {
            // region Carousels
            CompositionCategory.Carousel -> {
                // todo - accesses carouselCompositions.composition directly
                return carouselOfImagesManager.createComposition(
                    gson.fromJson(request.jsonData, CreateCarouselBasicImagesReq::class.java), layoutId, userId
                )
            }
            // endregion

            // region Just texts
            CompositionCategory.Markdown -> TODO()
            CompositionCategory.Text -> TODO()
            // endregion

            // region Banners
            CompositionCategory.Banner -> {
                TODO()
//                bannerRepository.createBannerBasic(
//                    gson.fromJson(request.jsonData, BannerBasic::class.java)
//                )
            }
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