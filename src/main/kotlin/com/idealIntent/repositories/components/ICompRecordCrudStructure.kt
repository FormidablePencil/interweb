package com.idealIntent.repositories.components

import com.idealIntent.serialized.libOfComps.RecordUpdate

/**
 * Structure for CRUD operations on libOfComps
 *
 * for examples:
 * @see com.idealIntent.repositories.components.TextRepository
 * @see com.idealIntent.repositories.components.ImageRepository
 * @see com.idealIntent.repositories.components.PrivilegeRepository
 * @see com.idealIntent.repositories.components.CarouselRepository
 *
 * @param Record Generic type for data such as Image, Text, PrivilegedAuthor, etc.
 * @param Collection Collection which records are associated under such as ImageCollection, TextCollection, PrivilegesCollection, etc.
 * @param RecordCollection DTO Collection of Records.
 * @constructor Create empty structure
 */
interface ICompRecordCrudStructure<Record, Collection, RecordCollection> {

    // region Get
    /**
     * Get records by collection id
     *
     * @param collectionId get records under collection's id
     * @return records under collectionId or null if failed to find by [collectionId]
     */
    fun getAssortmentById(collectionId: Int): RecordCollection

    /**
     * Get only collection record and not it's associated records
     *
     * @param id id of collection
     * @return collection but not associated records or null if failed to find by [id]
     */
    fun getCollection(id: Int): Collection?
    // endregion Get


    // region Insert
    /**
     * Insert record under a new collection
     *
     * @param record record to insert
     * @param collectionOf name the collection
     * @return collectionId or null if failed to insert [record]
     */
    fun insertNewRecord(record: Record, collectionOf: String): Int?

    /**
     * Batch insert [records] under a new collection
     *
     * @param records list of records to insert
     * @param collectionOf name the collection
     * @return collectionId or null if failed to insert [records]
     */
    fun batchInsertNewRecords(records: List<Record>, collectionOf: String): Int?

    /**
     * Insert record
     *
     * @param record record to insert
     * @param collectionId id of collection to insert [record] under
     * @return success or fail in insertion
     */
    fun insertRecord(record: Record, collectionId: Int): Boolean

    /**
     * Batch insert records
     *
     * @param records
     * @param collectionId id to identify under what collection to insert [records]
     * @return success or fail in inserting [records]
     */
    fun batchInsertRecords(records: List<Record>, collectionId: Int): Boolean

    /**
     * Insert collection [collectionOf] in order to group new records under
     *
     * @param collectionOf name the collection
     * @return collectionId or null if failed to insert new collection of [collectionOf]
     */
    fun insertRecordCollection(collectionOf: String): Int
    // endregion Insert


    // region Update
    /**
     * Update record
     *
     * @param collectionId id to identify under what collection [record] is under
     * @param record update to
     * @return success or fail in updating [record]
     */
    fun updateRecord(collectionId: Int, record: RecordUpdate): Boolean

    /**
     * Batch update records
     *
     * @param collectionId id to identify under what collection [records] are under
     * @param records update to
     */
    fun batchUpdateRecords(collectionId: Int, records: List<RecordUpdate>): Boolean
    // endregion Update


    // todo deletes
    // region Delete
    /**
     * Delete an image of collection
     *
     */
    fun deleteRecord(collectionId: Int): Boolean

    /**
     * Delete images of collection
     *
     */
    fun batchDeleteRecords(collectionId: Int): Boolean

    /**
     * Delete all images of collection
     *
     */
    fun deleteAllRecordsInCollection(collectionId: Int)

    /**
     * Delete image_collection and it's images
     *
     */
    fun deleteCollectionOfRecords()
    // endregion Delete
}