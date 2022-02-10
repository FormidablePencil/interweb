package com.idealIntent.managers.compositions

import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition

/**
 * Directs CRUD operations of composition types.
 *
 * While [composition category manager][ICompositionCategoryManagerStructure] directs actions of the category of
 * compositions, [composition type manager][ICompositionTypeManagerStructure] directs actions of the type of category,
 *
 * @param Composition Records of composition.
 * @param CompositionMetadata Composition information, ids, ect.
 * @param CreateRequest Ids of compositions and collections to compose and raw data to save before composition.
 * @param ComposePrepared Ids of composition and collections to created beforehand.
 */
interface ICompositionTypeManagerStructure<Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response> {

    fun getPublicComposition(compositionSourceId: Int): Composition?

    fun getPrivateComposition(compositionSourceId: Int, authorId: Int): Composition?

    /**
     * Firstly creates composition's collections and compositions (side note - some compositions are nested in one another),
     * inserts the records and creates a record to collection relationship.
     * Then creates privileges for the composition and assigns them to author by specified authorIds their privileges.
     * Then takes the collection and composition ids and sends them off to compose. If everything went
     * well the user will get the id of the newly composed composition.
     *
     * @param createRequest Composition of records.
     * @param authorId Id of user to validate that they are privileged.
     * @return CollectionId or null if failed.
     */
    fun createComposition(
        createRequest: CreateRequest,
        layoutId: Int,
        authorId: Int
    ): Int

    /**
     * Update composition
     *
     * @param compositionUpdateQue Update que. Gives you what column to update and to what value.
     * @param authorId Id of author to get only the composition they are privileged to update.
     * @throws
     */
    fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    )

    /**
     * Delete composition and records its composed.
     */
    fun deleteComposition(compositionSourceId: Int, authorId: Int)
}