package com.idealIntent.managers.compositions

import dtos.compositions.carousels.CompositionCarousel

// todo - will create CompositionCategoryManager for each category and move related logic there such as createComposition[Category], updateComposition[Category], deleteComposition[Category], getSingleCompositionOfPrivilegedAuthor[Category]
//  once the library of compositions get too vast for one file to contain

/**
 * I composition
 */
//Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response
interface ICompositionCategoryManagerStructure<Composition, Response> {

    /**
     * Find the composition to of compositionType and sends the composition compositionSourceId to composition's manager to get composition.
     *
     * @param compositionType
     * @param jsonData
     * @return
     */
    fun getPrivateComposition(compositionType: CompositionCarousel, compositionSourceId: Int, authorId: Int): Composition?

    /**
     * Find the composition to of compositionType and sends the json data off to the composition's manager to create composition.
     *
     * @param compositionType
     * @param jsonData
     * @return
     */
    fun createCompositionOfCategory(
        compositionType: CompositionCarousel, jsonData: String,
        layoutId: Int,
        userId: Int
    ): Response

    fun updateComposition()

    fun deleteComposition()
}