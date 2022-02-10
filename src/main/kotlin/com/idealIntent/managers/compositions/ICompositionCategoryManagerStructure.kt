package com.idealIntent.managers.compositions

import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition

// todo - will create CompositionCategoryManager for each category and move related logic there such as createComposition[Category], updateComposition[Category], deleteComposition[Category], getSingleCompositionOfPrivilegedAuthor[Category]
//  once the library of compositions get too vast for one file to contain

/**
 * I composition
 */
//Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response
interface ICompositionCategoryManagerStructure<CompositionType, Composition, Response> {

    /**
     * Find the composition to of compositionType and sends the composition compositionSourceId to composition's manager to get composition.
     *
     * @param compositionType
     * @param jsonData
     * @return
     */
    fun getPrivateComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
        authorId: Int
    ): Composition?

    /**
     * Find the composition to of compositionType and sends the json data off to the composition's manager to create composition.
     *
     * @param compositionType
     * @param jsonData
     * @return
     */
    fun createComposition(
        compositionType: CompositionType, jsonData: String,
        layoutId: Int,
        userId: Int
    ): Response

    fun updateComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    )

    fun deleteComposition(
        compositionType: CompositionType,
        compositionSourceId: Int,
        authorId: Int
    ): Boolean
}