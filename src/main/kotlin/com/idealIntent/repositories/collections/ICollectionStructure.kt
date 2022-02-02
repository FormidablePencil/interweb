package com.idealIntent.repositories.collections

import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import org.ktorm.database.Database

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
    val database: Database
    // region Get
    /**
     * Get a single record
     *
     * @param recordId
     * @param collectionId Still pass id since records are constrained by privileges. Somewhere up the ladder there's a
     * privilege to collection by [collectionId] validator.
     * @return Single record or null if not found by [recordId]
     */
    fun getRecordOfCollection(recordId: Int, collectionId: Int): Record? {
        val records = getRecordsQuery(recordId, collectionId)
        return if (records.isNotEmpty()) records.first() else null
    }

    /**
     * Get records by collection id.
     *
     * @param collectionId Get records under collection's id
     * @return Records under collection or null if failed to find by [collectionId]
     */
    fun getCollectionOfRecords(collectionId: Int): Pair<List<Record>, Int> {
        // todo - check privileges if allowed for any author or whether authorId is privileged
        val images = getRecordsQuery(null, collectionId)
        return Pair(images, collectionId)
    }

    fun getRecordsQuery(recordId: Int? = null, collectionId: Int): List<Record>

    /**
     * Get records to collection info.
     *
     * @param recordId []
     * @param collectionId
     */
    fun getRecordToCollectionInfo(recordId: Int, collectionId: Int): RecordToCollectionEntity?
    // endregion Get


    // region Insert

    // region todo - move to manager lvl
    /**
     * Insert [record] under a new collection
     *
     * @param record Record to insert
     * @return An id of newly created collection, id of image and image rank position to
     * discern image id of or null if failed to insert [record]
     */
//    fun insertNewRecord(record: Record): Int?

    /**
     * Batch insert [records] and add a relationship between [records] and a new collection.
     *
     * @param records List of records to insert.
     * @return An id of newly created collection, ids of images and images rank position to
     * discern images ids of or null if failed to insert [records]
     */
//    fun batchInsertNewRecords(records: List<Record>): List<Int>?
    // endregion

    fun batchInsertRecordsToNewCollection(records: List<Record>): Pair<List<Record>, Int>? {
        database.useTransaction { // todo - handle transaction throw exception
            val aRecords = batchInsertRecords(records)
            val collectionId = addRecordCollection()
            if (!batchAssociateRecordsToCollection(aRecords, collectionId))
                TODO("Throw a pretty exception")
            return Pair(aRecords, collectionId)
        }
    }

    /**
     * Insert [record] and return [record] with a generated id.
     *
     * @param record
     * @return Add generated id to [record] and return it or null if failed.
     */
    fun insertRecord(record: Record): Record?

    /**
     * Insert [records] and return [records] with a generated ids.
     *
     * @param records
     * @return Add generated ids corresponding to [records] and return it or null if failed.
     */
    fun batchInsertRecords(records: List<Record>): List<Record>

    /**
     * Insert a new collection to associate records to.
     *
     * Method would have been private if it was not of [interface][ICollectionStructure].
     *
     * @return collection id
     */
    fun addRecordCollection(): Int

    /**
     * Batch create record to collection relationship
     *
     * @param records
     * @param collectionId
     * @return Success or fail
     */
    fun batchAssociateRecordsToCollection(records: List<Record>, collectionId: Int): Boolean

    /**
     * Create record to collection relationship
     *
     * @param recordToCollection record to collection association
     * @return Success or fail
     */
    fun associateRecordToCollection(recordToCollection: RecordToCollection): Boolean
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
     * Delete a record from collection of [collectionId].
     */
    fun deleteRecord(recordId: Int, collectionId: Int): Boolean

    /**
     * Delete records from collection of [collectionId].
     */
    fun batchDeleteRecords(id: Int, collectionId: Int): Boolean

    /**
     * Delete all records from collection of [collectionId].
     */
    fun deleteAllRecordsInCollection(collectionId: Int)

    /**
     * Disassociate all records from collection of [collectionId].
     */
    fun disassociateRecordFromCollection(recordId: Int, collectionId: Int)

    /**
     * Delete only the collection and its relationship to records but not the records themselves.
     */
    fun deleteCollectionButNotRecord()

    // endregion Delete

    /**
     * validate image to collection relationship.
     *
     * Method would have been private if it wasn't of [interface][ICollectionStructure].
     */
    fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean =
        getRecordToCollectionInfo(recordId, collectionId) != null
}