package com.idealIntent.repositories.compositions

import com.idealIntent.dtos.compositions.RecordUpdate

/**
 * Structure for CRUD operations on compositions
 *
 * A CMS requires CRUD operations on compositions. This is an interface for all CRUD operational repositories of compositions
 * to adhere to for structure and CRUD operation requirements.
 *
 * @param MetadataOfComposition Metadata of collection such as ImagesCarousels
 * @param Composition Generic for compositions DTOs
 * @constructor Create empty structure for compositions
 */
interface ICompositionStructure<MetadataOfComposition, Composition> {

    // region Get
    /**
     * Get composition records by collection id
     *
     * @param id Get composition records under composition's id
     * @return Composition records under [id] or null if failed to find by [id]
     */
    fun getComposition(id: Int): Composition

    /**
     * Get only composition's metadata and not it's associated records
     *
     * @param id ID of composition
     * @return Composition's metadata and not associated records or null if failed to find by [id]
     */
    fun getMetadataOfComposition(id: Int): MetadataOfComposition?
    // endregion Get


    // region Insert
    /**
     * Insert [composition] under a new collection
     *
     * @param composition Composition to insert
     * @return CollectionId or null if failed to insert [composition]
     */
    fun insertComposition(composition: Composition): Int?

    /**
     * Batch insert [compositions] under a new collection
     *
     * @param compositions Compositions to insert
     * @param label Name the collection
     * @return CollectionId or null if failed to insert [compositions]
     */
    fun insertCompositions(compositions: List<Composition>, label: String): Int?
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
    fun deleteComposition(id: Int): Boolean
    fun batchDeleteCompositions(id: Int): Boolean
    // endregion Delete
}