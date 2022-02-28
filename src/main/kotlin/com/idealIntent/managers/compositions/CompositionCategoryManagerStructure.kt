package com.idealIntent.managers.compositions

import com.google.gson.Gson
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition

// todo CompositionResponse not being used
abstract class CompositionCategoryManagerStructure<CompositionType, Composition, Response> :
    ICompositionCategoryManagerStructure<CompositionType, Composition, Response> {
    val gson = Gson()
}

//Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response

/**
 * Directs CRUD operations of composition categories by [CompositionType].
 *
 * While [composition type manager][ICompositionTypeManagerStructure] directs actions of the type of category,
 * [composition category manager][ICompositionCategoryManagerStructure] directs actions of the category of compositions.
 *
 * E.g. If a deletion requested on a composition of a Carousel category, the delete method of
 * [CarouselsManager][com.idealIntent.managers.compositions.carousels.CarouselsManager]
 * which in turn will basically do the same but for type of category.
 */
private interface ICompositionCategoryManagerStructure<CompositionType, Composition, Response> {

    /**
     * Get public composition.
     *
     * Find the composition to of compositionType and sends the composition compositionSourceId to composition's manager to get composition.
     *
     * @param compositionType The type of composition of category such as Basic Images of Carousel.
     * @param compositionSourceId Id of composition's source and not the composition's id itself.
     * @return Composition of records.
     */
    fun getPublicComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
    ): Composition?

    /**
     * Get private composition.
     *
     * Find the composition to of compositionType and sends the composition compositionSourceId to composition's manager to get composition.
     *
     * @param compositionType The type of composition of category such as Basic Images of Carousel.
     * @param compositionSourceId Id of composition's source and not the composition's id itself.
     * @param authorId Only retrieves composition if author is privileged to get private composition.
     * @return Composition of records.
     */
    fun getPrivateComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
        authorId: Int
    ): Composition?

    /**
     * Find the composition to of compositionType and sends the json data off to the composition's manager to create composition.
     *
     * @return Id of newly created source composition.
     * @throws CompositionException [FailedToFindAuthorByUsername], [NotPrivilegedToLayout].
     * @see [ICompositionTypeManagerStructure.createComposition]
     */
    fun createComposition(
        compositionType: CompositionType, jsonData: String,
        layoutId: Int,
        authorId: Int
    ): Int

    /**
     * Update composition
     *
     * @throws CompositionException [ModifyPermittedToAuthorOfCompositionNotFound], [IdOfRecordProvidedNotOfComposition],
     * [ProvidedStringInPlaceOfInt].
     * @see ICompositionTypeManagerStructure.updateComposition
     */
    fun updateComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    )

    /**
     * Delete composition
     *
     * @throws CompositionException [CompositionNotFound]
     * @see ICompositionRepositoryStructure.deleteComposition
     */
    fun deleteComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
        authorId: Int
    )
}