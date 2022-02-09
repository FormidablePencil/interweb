package com.idealIntent.services

import com.google.gson.Gson
import com.idealIntent.dtos.compositionCRUD.BatchUpdateCompositionRequest
import com.idealIntent.dtos.compositionCRUD.BatchUpdateCompositionsRequest
import com.idealIntent.dtos.compositionCRUD.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositionCRUD.UpdateCompositionRequest
import com.idealIntent.dtos.compositions.UserComposition
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.SpaceManager
import com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.banners.BannersManager
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.managers.compositions.grids.GridsManager
import com.idealIntent.managers.compositions.texts.TextsManager
import com.idealIntent.repositories.compositions.CompositionDataBuilder
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.carousels.CompositionCarousel
import dtos.compositions.grids.CompositionGrid
import dtos.compositions.texts.CompositionText
import dtos.space.IUserComposition

/**
 * Cms service.
 *
 * There are 5 levels of depth to composition logic.
 * [cms server][CompositionService],
 * [composition of category manager][com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure],
 * [composition of category repository][com.idealIntent.repositories.compositions.ICompositionRepositoryStructure],
 * [collection repositories][com.idealIntent.repositories.collections.ICollectionStructure].
 */
class CompositionService(
    private val carouselsManager: CarouselsManager,
    private val spaceManager: SpaceManager,
    private val spaceRepository: SpaceRepository,
    private val textsManager: TextsManager,
    private val bannersManager: BannersManager,
    private val gridsManager: GridsManager,
) {

    fun getAuthorsCompositions(userId: Int) {
        print("ok")
    }

    fun getCompositionOfSpace(spaceAddress: String) {
        // spaces table should hold a collection of all components, id of components and what component (whatComponent: Enum(value: Int))
    }

    fun getPublicLayoutOfCompositions(layoutId: Int): CompositionDataBuilder =
        spaceManager.getPublicLayoutOfCompositions(layoutId)

    fun getPrivateLayoutOfCompositions(layoutId: Int, authorId: Int): CompositionDataBuilder =
        spaceManager.getPrivateLayoutOfCompositions(layoutId, authorId)


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
        userComposition: UserComposition,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): CompositionResponse {
        // todo validate that userId has privileges to layoutId

        return when (userComposition.compositionCategory) {
            CompositionCategory.Text ->
                textsManager.createCompositionOfCategory(
                    compositionType = CompositionText.fromInt(userComposition.compositionType),
                    jsonData, layoutId, userId
                )
            CompositionCategory.Banner ->
                bannersManager.createCompositionOfCategory(
                    compositionType = CompositionBanner.fromInt(userComposition.compositionType),
                    jsonData, layoutId, userId
                )
            CompositionCategory.Grid ->
                gridsManager.createCompositionOfCategory(
                    compositionType = CompositionGrid.fromInt(userComposition.compositionType),
                    jsonData, layoutId, userId
                )
            CompositionCategory.Carousel ->
                carouselsManager.createCompositionOfCategory(
                    compositionType = CompositionCarousel.fromInt(userComposition.compositionType),
                    jsonData, layoutId, userId
                )
            CompositionCategory.Markdown -> TODO() // todo - part of text, remove
            CompositionCategory.Divider -> TODO() // todo - style and text, move to text
            CompositionCategory.LineDivider -> TODO() // todo - styles, remove
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

    // todo - response
    /**
     * Delete composition
     *
     * Given the composition category the method will call the corresponding manager. The user of [authorId] must be
     * privileged to delete composition of [compositionSourceId]. todo - exception catching could be done here.
     *
     * @param userComposition Category and type of category of composition to delete.
     * @param compositionSourceId Composition source id that is the source of the composition and its records.
     * @param authorId Id of the author who should be privileged to modify or delete composition.
     * @return Failed of success responses. todo
     * @see  ICompositionCategoryManagerStructure.deleteComposition
     */
    fun deleteComposition(
        userComposition: UserComposition,
        authorId: Int
    ): Boolean {
        val gson = Gson()
        return when (userComposition.compositionCategory) {
            CompositionCategory.Carousel ->
                carouselsManager.deleteComposition(
                    compositionType = CompositionCarousel.fromInt(userComposition.compositionType),
                    compositionSourceId = userComposition.compositionSourceId,
                    authorId = authorId,
                )
            CompositionCategory.Text ->
                textsManager.deleteComposition(
                    compositionType = CompositionText.fromInt(userComposition.compositionType),
                    compositionSourceId = userComposition.compositionSourceId,
                    authorId = authorId,
                )
            CompositionCategory.Banner ->
                bannersManager.deleteComposition(
                    compositionType = CompositionBanner.fromInt(userComposition.compositionType),
                    compositionSourceId = userComposition.compositionSourceId,
                    authorId = authorId,
                )
            CompositionCategory.Grid -> {
                gridsManager.deleteComposition(
                    compositionType = CompositionGrid.fromInt(userComposition.compositionType),
                    compositionSourceId = userComposition.compositionSourceId,
                    authorId = authorId,
                )
            }

            CompositionCategory.Markdown -> TODO() // todo - part of text, remove
            CompositionCategory.Divider -> TODO() // todo - style and text, move to text
            CompositionCategory.LineDivider -> TODO() // todo - styles, remove
        }
    }

    // region todo
    fun updateComposition(request: UpdateCompositionRequest) {
        request.updateComposition.map {
            updateComposition(it)
        }
    }

    fun <T> updateComposition(
        request: SingleUpdateCompositionRequest<T>
//        compositionUpdateQue = List<UpdateDataOfComposition<UpdateDataOfCarouselOfImages>>,
    ): Boolean {
        val gson = Gson()
        when (CompositionCategory.fromInt(request.compositionType)) {
            CompositionCategory.Carousel -> {
                carouselsManager.updateComposition(
                    compositionType = CompositionCarousel.fromInt(request.compositionType),
                    compositionSourceId = request.compositionSourceId,
                    compositionUpdateQue = request.updateDataOfComposition,
                    authorId = authorId
//                    componentId = request.id,
//                    column = CarouselOfImagesTABLE.fromInt(request.where[0].table),
//                    updateToData = request.updateToData
                )
            }
            CompositionCategory.Text -> TODO()
            CompositionCategory.Banner -> TODO()
            CompositionCategory.Grid -> TODO()
            CompositionCategory.Markdown -> TODO() // todo - part of text, remove
            CompositionCategory.Divider -> TODO() // todo - style and text, move to text
            CompositionCategory.LineDivider -> TODO() // todo - styles, remove
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
            CompositionCategory.Grid -> TODO()
            // endregion

            // region Dividers
            CompositionCategory.Divider -> TODO()
            CompositionCategory.LineDivider -> TODO()
            // endregion
        }
        return false
    }
    // endregion todo


    fun createSpaceWithNewLayout(layoutName: String) = spaceManager.createSpaceWithNewLayout(layoutName)

    fun createNewLayout(name: String): Int = spaceRepository.insertNewLayout(name)
}