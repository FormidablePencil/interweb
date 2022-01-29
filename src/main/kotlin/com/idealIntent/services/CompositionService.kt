package com.idealIntent.services

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.BatchUpdateCompositionRequest
import com.idealIntent.dtos.compositions.BatchUpdateCompositionsRequest
import com.idealIntent.dtos.compositions.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.UpdateCompositionRequest
import com.idealIntent.managers.CompositionManager
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.BannerBasic
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.carousels.CarouselOfImagesTABLE
import dtos.space.IUserComposition

class CompositionService(
    private val spaceRepository: SpaceRepository,
    private val componentManager: CompositionManager,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
    private val textRepository: TextRepository,
) {

    // todo - Response object
    fun insertComposition(request: IUserComposition, spaceAddress: String): Boolean {
        componentManager.insertComposition(request)
        TODO()
    }

    fun batchInsertCompositions(request: List<IUserComposition>, spaceAddress: String) {
        // todo - revert if some component fails to save. Save all or save non
        request.map {
            componentManager.insertComposition(it)
        }
    }


    fun deleteComposition(request: IUserComposition): Boolean {
        val gson = Gson()
        return when (request.compositionType) {
            // region Carousels
            CompositionCategory.Carousel ->
                TODO()
//                carouselOfImagesRepository.deleteComposition(
//                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java),
//                )
            // endregion

            // region Texts
            CompositionCategory.Markdown -> TODO()
            CompositionCategory.Text ->
                TODO("code")
//                bannerRepository.deleteBannerBasic(
//                    gson.fromJson(request.jsonData, BannerBasic::class.java)
//                )
            // endregion

            // region Banners
            CompositionCategory.Banner -> TODO()
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

    fun updateComposition(request: UpdateCompositionRequest) {
        request.updateComposition.map {
            updateComposition(it)
        }
    }

    fun updateComposition(request: SingleUpdateCompositionRequest): Boolean {
        val gson = Gson()
        when (CompositionCategory.fromInt(request.compositionType)) {
            // region Carousels
            CompositionCategory.Carousel -> {
                carouselOfImagesRepository.update(
                    componentId = request.id,
                    column = CarouselOfImagesTABLE.fromInt(request.where[0].table),
                    updateToData = request.updateToData
                )
            }
            // endregion

            // region Just texts
            CompositionCategory.Text -> TODO()
            CompositionCategory.Markdown -> TODO()
            // endregion

            // region Banners
            CompositionCategory.Banner -> TODO()
            // endregion

            // region Grids
            CompositionCategory.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionCategory.Divider -> TODO()
            CompositionCategory.LineDivider -> TODO()
            // endregion
        }
        return false
    }

    fun batchUpdateCompositions(request: BatchUpdateCompositionsRequest) {
        request.updateComposition.map {
            batchUpdateComposition(it)
        }
    }

    fun batchUpdateComposition(request: BatchUpdateCompositionRequest): Boolean {
        val gson = Gson()
        when (CompositionCategory.fromInt(request.compositionType)) {
            // region Carousels
            CompositionCategory.Carousel -> {
                carouselOfImagesRepository.batchUpdate(
                    componentId = request.id,
                    table = CarouselOfImagesTABLE.fromInt(request.where[0].table),
                    updateToData = request.updateToData
                )
            }
            // endregion

            // region Just texts
            CompositionCategory.Markdown,
            CompositionCategory.Text ->
                textRepository.batchUpdateRecords(
                    records = request.updateToData,
                    id = request.id
                )
            // endregion

            // region Banners
            CompositionCategory.Banner -> TODO()
            // endregion

            // region Grids
            CompositionCategory.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionCategory.Divider -> TODO()
            CompositionCategory.LineDivider -> TODO()
            // endregion
        }
        return false
    }
}