package com.idealIntent.managers

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.BatchUpdateCompositionRequest
import com.idealIntent.dtos.compositions.BatchUpdateCompositionsRequest
import com.idealIntent.dtos.compositions.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.UpdateCompositionRequest
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.BannerBasic
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.carousels.CarouselOfImagesTABLE
import dtos.space.IUserComposition

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

    fun insertComposition(request: IUserComposition): Boolean {
        val gson = Gson()
        return when (request.compositionType) {
            // region Carousels
            CompositionCategory.Carousel -> {
                // todo - accesses carouselCompositions.composition directly
                return carouselOfImagesRepository.insertNewComposition(
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