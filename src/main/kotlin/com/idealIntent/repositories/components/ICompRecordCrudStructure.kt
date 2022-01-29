package com.idealIntent.repositories.components

import com.idealIntent.serialized.libOfComps.RecordUpdate

/**
 * Structure for CRUD operations on libOfComps
 *
 * @param Record generic type for data such as Image, Text, PrivilegedAuthor, etc.
 * @constructor Create empty structure
 */
interface ICompRecordCrudStructure<Record> {

    // region Get
    /**
     * Get items by collection id
     *
     * @param collectionId get items under collection's id
     * @return items under collectionId or null if failed to find by [collectionId]
     */
    fun getAssortmentById(collectionId: Int): Any

    /**
     * Get only text_collection and not it's associated items
     *
     * @param id id of collection
     * @return collection but not associated items or null if failed to find by [id]
     */
    fun getCollection(id: Int): Any?
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


    // region Delete
    // endregion Delete
}