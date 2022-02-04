package com.idealIntent.services

import com.google.gson.Gson
import com.idealIntent.dtos.compositionCRUD.BatchUpdateCompositionRequest
import com.idealIntent.dtos.compositionCRUD.BatchUpdateCompositionsRequest
import com.idealIntent.dtos.compositionCRUD.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositionCRUD.UpdateCompositionRequest
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import dtos.compositions.CompositionCategory
import dtos.space.IUserComposition

/**
 * Cms service.
 *
 * There are 5 levels of depth to composition logic.
 * [cms server][CmsService],
 * [composition of category manager][com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure],
 * [composition of category repository][com.idealIntent.repositories.compositions.ICompositionRepoStructure],
 * [collection repositories][com.idealIntent.repositories.collections.ICollectionStructure].
 */
class CmsService(
    private val carouselsManager: CarouselsManager,
) {

    fun getAuthorsCompositions(authorId: Int) {

    }

    fun getCompositionOfSpace(spaceAddress: String) {
        // spaces table should hold a collection of all components, id of components and what component (whatComponent: Enum(value: Int))
//        compositionManager.getCompositionOfSpace(spaceAddress)
    }


    fun createComposition(compositionCategory: Int, jsonData: String, userId: Int): CompositionResponse {
        return when (CompositionCategory.fromInt(compositionCategory)) {
            CompositionCategory.Text -> TODO()
            CompositionCategory.Markdown -> TODO()
            CompositionCategory.Banner -> TODO()
            CompositionCategory.OneOffGrid -> TODO()
            CompositionCategory.Divider -> TODO()
            CompositionCategory.LineDivider -> TODO()
            CompositionCategory.Carousel ->
                carouselsManager.createCompositionOfCategory(compositionCategory, jsonData, userId)
        }
    }

    /**
     * Batch insert compositions
     *
     * @param request
     * @param spaceAddress
     */
    fun insertCompositions(request: List<IUserComposition>, spaceAddress: String) {
        // todo - revert if some component fails to save. Save all or save non
        TODO()
//        request.map {
//            compositionManager.createComposition(it.compositionType)
//        }
    }

    /**
     * Insert composition
     *
     * @param request
     * @param spaceAddress
     * @return
     */


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
//                carouselOfImagesRepository.update(
//                    componentId = request.id,
//                    column = CarouselOfImagesTABLE.fromInt(request.where[0].table),
//                    updateToData = request.updateToData
//                )
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
//                carouselOfImagesRepository.batchUpdate(
//                    componentId = request.id,
//                    table = CarouselOfImagesTABLE.fromInt(request.where[0].table),
//                    updateToData = request.updateToData
//                )
            }
            // endregion

            // region Just texts
            CompositionCategory.Markdown,
            CompositionCategory.Text ->
                TODO()
//                textRepository.batchUpdateRecords(
//                    records = request.updateToData,
//                    collectionId = request.id
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
        return false
    }
}