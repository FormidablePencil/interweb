package com.idealIntent.services

import com.google.gson.Gson
import com.idealIntent.dtos.compositionCRUD.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.ExistingUserComposition
import com.idealIntent.dtos.compositions.NewUserComposition
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
import dtos.compositions.CompositionCategory.*
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.carousels.CompositionCarousel
import dtos.compositions.grids.CompositionGrid
import dtos.compositions.texts.CompositionText

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
    val gson = Gson()

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
        userComposition: NewUserComposition,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): CompositionResponse {
        // todo validate that userId has privileges to layoutId
        with(userComposition) {
            return when (compositionCategory) {
                Text ->
                    textsManager.createComposition(
                        compositionType = CompositionText.fromInt(compositionType),
                        jsonData, layoutId, userId
                    )
                Banner ->
                    bannersManager.createComposition(
                        compositionType = CompositionBanner.fromInt(compositionType),
                        jsonData, layoutId, userId
                    )
                Grid ->
                    gridsManager.createComposition(
                        compositionType = CompositionGrid.fromInt(compositionType),
                        jsonData, layoutId, userId
                    )
                Carousel ->
                    carouselsManager.createComposition(
                        compositionType = CompositionCarousel.fromInt(compositionType),
                        jsonData, layoutId, userId
                    )
                Markdown -> TODO() // todo - part of text, remove
                Divider -> TODO() // todo - style and text, move to text
                LineDivider -> TODO() // todo - styles, remove
            }
        }
    }

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
        userComposition: ExistingUserComposition,
        authorId: Int
    ): Boolean {
        with(userComposition) {
            return when (userComposition.compositionCategory) {
                Text ->
                    textsManager.deleteComposition(
                        compositionType = CompositionText.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        authorId = authorId,
                    )
                Banner ->
                    bannersManager.deleteComposition(
                        compositionType = CompositionBanner.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        authorId = authorId,
                    )
                Grid ->
                    gridsManager.deleteComposition(
                        compositionType = CompositionGrid.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        authorId = authorId,
                    )
                Carousel ->
                    carouselsManager.deleteComposition(
                        compositionType = CompositionCarousel.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        authorId = authorId,
                    )

                Markdown -> TODO() // todo - part of text, remove
                Divider -> TODO() // todo - style and text, move to text
                LineDivider -> TODO() // todo - styles, remove
            }
        }
    }

    fun updateComposition(request: SingleUpdateCompositionRequest): Boolean {
        with(request) {
            when (CompositionCategory.fromInt(compositionType)) {
                Text ->
                    textsManager.updateComposition(
                        compositionType = CompositionText.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        compositionUpdateQue = compositionUpdateQue,
                        authorId = authorId
                    )
                Banner ->
                    bannersManager.updateComposition(
                        compositionType = CompositionBanner.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        compositionUpdateQue = compositionUpdateQue,
                        authorId = authorId
                    )
                Grid ->
                    gridsManager.updateComposition(
                        compositionType = CompositionGrid.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        compositionUpdateQue = compositionUpdateQue,
                        authorId = authorId
                    )
                Carousel ->
                    carouselsManager.updateComposition(
                        compositionType = CompositionCarousel.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        compositionUpdateQue = compositionUpdateQue,
                        authorId = authorId
                    )

                Markdown -> TODO() // todo - part of text, remove
                Divider -> TODO() // todo - style and text, move to text
                LineDivider -> TODO() // todo - styles, remove
            }
            return false
        }
    }

    // todo, may not need...
    fun createNewSpace(layoutName: String, authorId: Int) =
        spaceManager.createSpace(layoutName, authorId)

    fun createNewLayout(name: String, authorId: Int): Int {
        val layoutId = spaceRepository.insertNewLayout(name)
        TODO("associate layout to account through author id")
        return layoutId
    }
}