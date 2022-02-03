package com.idealIntent.repositories.compositions

import com.idealIntent.dtos.compositionCRUD.RecordUpdate

/**
 * Structure for CRUD operations on compositions
 *
 * SpaceResponseFailed CMS requires CRUD operations on compositions. This is an interface for all CRUD operational repositories of compositions
 * to adhere to for structure and CRUD operation requirements.
 *
 * @param Composition Records of composition.
 * @param CompositionMetadata Composition information, ids, ect.
 * @param CreateRequest Ids of compositions and collections to compose and raw data to save before composition.
 * @param ComposePrepared Ids of composition and collections to created beforehand.
 */
interface ICompositionManagerStructure<Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response> {

    // region Insert
    /**
     * Insert [composition] under a new collection
     *
     * @param composition Composition to insert
     * @return CollectionId or null if failed to insert [composition]
     */
    fun createComposition(createRequest: CreateRequest): Response
    // endregion Insert


    // region Update
    /**
     * Update composition
     *
     * @param id ID to identify under what collection [record] is under
     * @param record Update to
     * @return Success or fail in updating [record]
     */
    fun updateComposition(id: Int, record: RecordUpdate): Boolean
    // todo - reimplement this to work with compositions

    /**
     * Batch update compositions
     *
     * @param id ID to identify under what collection [records] are under
     * @param records Update to
     */
    fun batchUpdateCompositions(id: Int, records: List<RecordUpdate>): Boolean
    // todo - reimplement this to work with compositions

    // endregion Update


    // todo deletes
    // region Delete
    // endregion Delete
}