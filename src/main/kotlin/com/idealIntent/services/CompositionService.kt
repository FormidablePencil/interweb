package com.idealIntent.services

import com.google.gson.Gson
import com.idealIntent.dtos.compositionCRUD.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.ExistingUserComposition
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.SpaceManager
import com.idealIntent.managers.compositions.banners.BannersManager
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.managers.compositions.grids.GridsManager
import com.idealIntent.managers.compositions.headers.HeadersManager
import com.idealIntent.managers.compositions.texts.TextsManager
import com.idealIntent.repositories.compositions.CompositionDataBuilder
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.CompositionCategory.*
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.carousels.CompositionCarouselType
import dtos.compositions.grids.CompositionGrid
import dtos.compositions.headers.CompositionHeader
import dtos.compositions.texts.CompositionTextType
import io.ktor.http.*

/**
 * Cms service.
 *
 * There are 5 levels of depth to composition logic.
 *
 * 1. [composition service][CompositionService], directs CRUD operations of the categories of compositions.
 * 2. [composition of category manager][com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure],
 * directs CRUD operations of the category of compositions.
 * 3. [composition of type of category manager][com.idealIntent.managers.compositions.ICompositionTypeManagerStructure],
 * directs CRUD operations of the type of category of compositions.
 * 4. [composition of type of category repositories][com.idealIntent.repositories.compositions.SimpleCompositionRepositoryStructure],
 * does CRUD operations of the type of category of compositions.
 * 5. [collection repositories][com.idealIntent.repositories.collections.ICollectionStructure].
 * does CRUD operations of compositions and collections composed of composition request to do on.
 */
class CompositionService(
    private val spaceManager: SpaceManager,
    private val spaceRepository: SpaceRepository,
    private val textsManager: TextsManager,
    private val bannersManager: BannersManager,
    private val gridsManager: GridsManager,
    private val carouselsManager: CarouselsManager,
    private val headersManager: HeadersManager,
) {
    val gson = Gson()

    // region todo
    fun getCompositionOfSpace(spaceAddress: String) {
        // spaces table should hold a collection of all components, id of components and what component (whatComponent: Enum(value: Int))
        // todo query getPublicLayoutOfCompositions but by space
    }

    fun getPublicLayoutOfCompositions(layoutId: Int): CompositionResponse {
        TODO("All layouts should be private and spaces be public")
//        spaceManager.getPublicLayoutOfCompositions(layoutId)
    }

    fun getPrivateLayoutOfCompositions(layoutId: Int, authorId: Int): CompositionResponse {
        val res: CompositionDataBuilder = spaceManager.getPrivateLayoutOfCompositions(layoutId, authorId)
        return CompositionResponse().succeeded(HttpStatusCode.OK)
    }

    fun createNewSpace(layoutName: String, authorId: Int) = CompositionResponse().succeeded(
        HttpStatusCode.Created, spaceManager.createSpace(layoutName, authorId)
    )
    // endregion todo

    /**
     * Create new composition layout.
     *
     * Should always succeed.
     *
     * @param name User given name for layout.
     * @param authorId Id to associate absolute privileges to.
     */
    fun createNewLayout(name: String, authorId: Int) = CompositionResponse().succeeded(
        HttpStatusCode.Created, spaceRepository.insertNewLayout(name, authorId)
    )

    /**
     * Create composition under an existing layout.
     *
     * Create composition given the category and type. If fails, [CompositionException] will be thrown and caught along
     * with data specifying where the request went wrong.
     *
     * @param userComposition The category and type to cast [compositionSerialized] and save create as.
     * @param compositionSerialized The composition data serialized as json to pass along this cms tree easily. It will be
     * decentralized and cast to its respective types.
     * @param layoutId Id of layout to associate composition to.
     * @param userId AuthorId to given absolute privileges to.
     * @return Response of [HttpStatusCode.Created] and source id of newly created composition.
     * @see ICompositionTypeManagerStructure.createComposition
     */
    fun createComposition(
        userComposition: NewUserComposition,
        compositionSerialized: String,
        layoutId: Int,
        userId: Int
    ): CompositionResponse {
        try {
            if (!spaceRepository.validateAuthorPrivilegedToModifyLayout(layoutId, userId))
                throw CompositionException(CompositionCode.NotPrivilegedToLayout)

            with(userComposition) {
                val compositionSourceId = when (compositionCategory) {
                    Text ->
                        textsManager.createComposition(
                            compositionType = CompositionTextType.fromInt(compositionType),
                            compositionSerialized, layoutId, userId
                        )
                    Banner ->
                        bannersManager.createComposition(
                            compositionType = CompositionBanner.fromInt(compositionType),
                            compositionSerialized, layoutId, userId
                        )
                    Grid ->
                        gridsManager.createComposition(
                            compositionType = CompositionGrid.fromInt(compositionType),
                            compositionSerialized, layoutId, userId
                        )
                    Carousel ->
                        carouselsManager.createComposition(
                            compositionType = CompositionCarouselType.fromInt(compositionType),
                            compositionSerialized, layoutId, userId
                        )

                    Markdown -> TODO() // todo - part of text, remove
                    Divider -> TODO() // todo - style and text, move to text
                    LineDivider -> TODO() // todo - styles, remove
                    Header -> headersManager.createComposition(
                        compositionType = CompositionHeader.fromInt(compositionType),
                        compositionSerialized, layoutId, userId
                    )
                }
                return CompositionResponse().succeeded(HttpStatusCode.Created, compositionSourceId)
            }
        } catch (ex: CompositionException) {
            return when (ex.code) {
                CompositionCode.NotPrivilegedToLayout,
                CompositionCode.FailedToFindAuthorByUsername ->
                    CompositionResponse().failed(ex.code, ex.moreDetails)
                else ->
                    throw CompositionExceptionReport(CompositionCode.ServerError, this::class.java, ex)
            }
        }
    }

    /**
     * Update composition.
     *
     * @see ICompositionRepositoryStructure.deleteComposition
     */
    fun updateComposition(request: SingleUpdateCompositionRequest): CompositionResponse {
        try {
            with(request) {
                when (CompositionCategory.fromInt(compositionType)) {
                    Text ->
                        textsManager.updateComposition(
                            compositionType = CompositionTextType.fromInt(compositionType),
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
                            compositionType = CompositionCarouselType.fromInt(compositionType),
                            compositionSourceId = compositionSourceId,
                            compositionUpdateQue = compositionUpdateQue,
                            authorId = authorId
                        )

                    Markdown -> TODO() // todo - part of text, remove
                    Divider -> TODO() // todo - style and text, move to text
                    LineDivider -> TODO() // todo - styles, remove
                    Header -> headersManager.updateComposition(
                        compositionType = CompositionHeader.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        compositionUpdateQue = compositionUpdateQue,
                        authorId = authorId
                    )
                }
            }
            return CompositionResponse().succeeded(HttpStatusCode.OK)
        } catch (ex: CompositionException) {
            return when (ex.code) {
                CompositionCode.ModifyPermittedToAuthorOfCompositionNotFound,
                CompositionCode.IdOfRecordProvidedNotOfComposition,
                CompositionCode.ProvidedStringInPlaceOfInt ->
                    CompositionResponse().failed(ex.code, ex.moreDetails)
                else ->
                    throw CompositionExceptionReport(CompositionCode.ServerError, this::class.java, ex)
            }
        }
    }

    /**
     * Delete composition but only if user of id privileged to do so.
     *
     * Given the composition category the method will call the corresponding manager...
     *
     * @param userComposition Category and type of category of composition to delete and composition source id.
     * @param authorId Id of the author who should be privileged to modify or delete composition.
     * @return Failed or success responses object.
     * @see ICompositionRepositoryStructure.deleteComposition
     */
    fun deleteComposition(
        userComposition: ExistingUserComposition,
        authorId: Int
    ): CompositionResponse {
        try {
            with(userComposition) {
                when (compositionCategory) {
                    Text ->
                        textsManager.deleteComposition(
                            compositionType = CompositionTextType.fromInt(compositionType),
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
                            compositionType = CompositionCarouselType.fromInt(compositionType),
                            compositionSourceId = compositionSourceId,
                            authorId = authorId,
                        )

                    Markdown -> TODO() // todo - part of text, remove
                    Divider -> TODO() // todo - style and text, move to text
                    LineDivider -> TODO() // todo - styles, remove
                    Header -> headersManager.deleteComposition(
                        compositionType = CompositionHeader.fromInt(compositionType),
                        compositionSourceId = compositionSourceId,
                        authorId = authorId
                    )
                }
            }
            return CompositionResponse().succeeded(HttpStatusCode.OK)
        } catch (ex: CompositionException) {
            return when (ex.code) {
                CompositionCode.CompositionNotFound ->
                    CompositionResponse().failed(ex.code, ex.moreDetails)
                else ->
                    throw CompositionExceptionReport(CompositionCode.ServerError, this::class.java, ex)
            }
        }
    }
}