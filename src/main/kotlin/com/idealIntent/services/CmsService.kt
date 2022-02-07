package com.idealIntent.services

import com.google.gson.Gson
import com.idealIntent.dtos.compositionCRUD.BatchUpdateCompositionRequest
import com.idealIntent.dtos.compositionCRUD.BatchUpdateCompositionsRequest
import com.idealIntent.dtos.compositionCRUD.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositionCRUD.UpdateCompositionRequest
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarousel
import dtos.space.IUserComposition

/**
 * Cms service.
 *
 * There are 5 levels of depth to composition logic.
 * [cms server][CmsService],
 * [composition of category manager][com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure],
 * [composition of category repository][com.idealIntent.repositories.compositions.ICompositionRepositoryStructure],
 * [collection repositories][com.idealIntent.repositories.collections.ICollectionStructure].
 */
class CmsService(
    private val carouselsManager: CarouselsManager,
    private val spaceRepository: SpaceRepository,
) {

    fun getAuthorsCompositions(userId: Int) {


    }

    fun getCompositionOfSpace(spaceAddress: String) {
        // spaces table should hold a collection of all components, id of components and what component (whatComponent: Enum(value: Int))
//        compositionManager.getCompositionOfSpace(spaceAddress)
    }


    /**
     * Create composition
     *
     * @param compositionCategory
     * @param compositionOfCategory
     * @param jsonData
     * @param userId
     * @return Id of created composition.
     */
    fun createComposition(
        compositionCategory: CompositionCategory,
        compositionOfCategory: CompositionCarousel,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): CompositionResponse {
        return when (compositionCategory) {
            CompositionCategory.Text -> TODO()
            CompositionCategory.Markdown -> TODO()
            CompositionCategory.Banner -> TODO()
            CompositionCategory.OneOffGrid -> TODO()
            CompositionCategory.Divider -> TODO()
            CompositionCategory.LineDivider -> TODO()
            CompositionCategory.Carousel ->
                carouselsManager.createCompositionOfCategory(compositionOfCategory, jsonData, layoutId, userId)
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

    fun getSpace() {
    }

    fun createSpace(layoutName: String) {
        val spaceAddress = spaceRepository.insertNewSpace()
        val layoutId = spaceRepository.insertNewLayout(layoutName)
        spaceRepository.associateLayoutToSpace(spaceAddress = spaceAddress, layoutId = layoutId)
    }

    fun createNewLayout(name: String): Int {
        return spaceRepository.insertNewLayout(name)
    }
}