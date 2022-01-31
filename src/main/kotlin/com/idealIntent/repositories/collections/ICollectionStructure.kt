package com.idealIntent.repositories.collections

import com.idealIntent.dtos.compositions.RecordUpdate

/**
 * Structure for CRUD operations on compositions' collections
 *
 * Methods of this interface is applicable only to low level specific such as ImageCollection, TextCollection and PrivilegesCollection
 *
 * @param Record DTO for Generic type for data such as Image, Text, PrivilegedAuthor, etc.
 * @param RecordToCollectionEntity Entity of collection table that hold the relationship between a record and collection by their ids.
 * @param RecordToCollection DTO for [RecordToCollectionEntity].
 * @param CollectionOfRecords DTO of [Record]s.
 * @constructor Create empty structure for collections
 */
interface ICollectionStructure<Record, RecordToCollectionEntity, RecordToCollection, CollectionOfRecords> {

    // region Get
    /**
     * Get records by collection id
     *
     * @param id Get records under collection's id
     * @return Records under [id] or null if failed to find by [id]
     */
    fun getCollectionOfRecords(recordId: Int, collectionId: Int): CollectionOfRecords

    /**
     * Get records to collection info
     *
     * @param recordId []
     * @param collectionId
     */
    fun getRecordsToCollectionInfo(recordId: Int, collectionId: Int): RecordToCollectionEntity?
    // endregion Get


    // region Insert
    /**
     * Insert [record] under a new collection
     *
     * @param record Record to insert
     * @return An id of newly created collection, id of image and image rank position to
     * discern image id of or null if failed to insert [record]
     */
    fun insertNewRecord(record: Record): RecordToCollection?

    /**
     * Batch insert [records] and add a relationship between [records] and a new collection.
     *
     * @param records List of records to insert.
     * @return An id of newly created collection, ids of images and images rank position to
     * discern images ids of or null if failed to insert [records]
     */
    fun batchInsertNewRecords(records: List<Record>): List<RecordToCollection>?

    /**
     * Insert [record] and add a [record] to collection relationship by provided [collectionId].
     *
     * @param record
     * @param collectionId The id of collection to associate [record] under.
     * @return Ids of record and collection and rank order to identify who's newly generated id is who's
     * or null if failed to insert [record].
     */
    fun insertRecord(record: Record, collectionId: Int): RecordToCollection?

    /**
     * Batch insert [records] and add a [records] to collection relationship by provided [collectionId].
     *
     * @param records
     * @param collectionId The id of collection to associate [records] under.
     * @return Ids of records and collection and image rank orders to identify who's newly generated id is who's
     * or null if failed to insert [records].
     */
    fun batchInsertRecords(records: List<Record>, collectionId: Int): List<RecordToCollection>?

    /**
     * Insert a new collection to associate records to.
     *
     * Method would have been private if it was not in [interface][ICollectionStructure].
     *
     * @return collection id
     */
    fun addRecordCollection(): Int
    // endregion Insert


    // region Update
    /**
     * Update record
     *
     * @param collectionId ID to identify under what collection [record] is under
     * @param record Update to
     * @return Success or fail in updating [record]
     */
    fun updateRecord(record: RecordUpdate, imageId: Int, collectionId: Int): Boolean

    /**
     * Batch update records
     *
     * @param collectionId ID to identify under what collection [records] are under
     * @param records Update to
     */
    fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean
    // endregion Update


    // todo deletes
    // region Delete
    /**
     * Delete an image of collection
     *
     */
    fun deleteRecord(recordId: Int, collectionId: Int): Boolean

    /**
     * Delete images of collection
     *
     */
    fun batchDeleteRecords(id: Int): Boolean

    /**
     * Delete all images of collection
     *
     */
    fun deleteAllRecordsInCollection(collectionId: Int)

    fun disassociateRecordFromCollection(recordId: Int, collectionId: Int)

    /**
     * Delete image_collection and it's images
     *
     */
    fun deleteCollectionButNotRecord()

    // endregion Delete


    /**
     * validate image to collection relationship.
     *
     * Method was to be private if it wasn't part of the [ICollectionStructure][ICollectionStructure].
     *
     * @param id ID of collection
     * @return Collection's metadata and not associated records or null if failed to find by [id]
     */
    fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean
}