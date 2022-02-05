package com.idealIntent.repositories.collections

import com.google.gson.Gson
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import org.ktorm.database.Database

/**
 * Structure for CRUD operations on compositions' collections
 *
 * Methods of this interface is applicable only to low level specific such as ImageCollection, TextCollection and PrivilegesCollection
 *
 * [Record] has no primary key [RecordPK], out has a primary key.
 *
 * @param Record DTO for Generic type for data such as Image, Text, PrivilegedAuthor, etc.
 * @param RecordPK DTO for Generic type for data such as Image, Text, PrivilegedAuthor, etc. but with a primary key.
 * @param RecordToCollectionEntity Entity of collection table that hold the relationship between a record and collection by their ids.
 * @param RecordToCollection DTO for [RecordToCollectionEntity].
 * @param CollectionOfRecords DTO of [Record]s.
 * @constructor Create empty structure for collections
 */
interface ICollectionStructure<Record, RecordPK, RecordToCollectionEntity, RecordToCollection, CollectionOfRecords> {
    val database: Database
    // region Get
    /**
     * Get a single record.
     *
     * @param recordId id of record.
     * @param collectionId id of collection.
     * @return Single record or null if not found.
     */
    fun getRecordOfCollection(recordId: Int, collectionId: Int): RecordPK? =
        getRecordsQuery(recordId, collectionId)?.first()

    /**
     * Get records by collection id.
     *
     * @param collectionId Get records under collection's id
     * @return A list of records under collection of [collectionId] or null if non found
     */
    fun getCollectionOfRecords(collectionId: Int): List<RecordPK>? = getRecordsQuery(null, collectionId)

    /**
     * Get records query. Not used directly but by [getRecordOfCollection] and [getCollectionOfRecords]
     *
     * @param recordId Id of record to look up null for returning only record
     * @param collectionId Get records under collection's id
     * @return A list of records under collection of [collectionId] or null if non found
     */
    fun getRecordsQuery(recordId: Int? = null, collectionId: Int): List<RecordPK>?

    /**
     * Get records to collection info.
     *
     * @param recordId Id of record.
     * @param collectionId
     */
    fun getRecordToCollectionRelationship(recordId: Int, collectionId: Int): RecordToCollectionEntity?

    /**
     * validate image to collection relationship.
     */
    fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean =
        getRecordToCollectionRelationship(recordId, collectionId) != null
    // endregion Get


    // region Insert
    /**
     * Batch insert records to new collection
     *
     * @param records Records to insert.
     * @return Records with a primary key and id of collection.
     * @throws batchInsertRecords [failed to insert a record][CompositionCode.FailedToInsertRecord]
     */
    @Throws(CompositionException::class)
    fun batchInsertRecordsToNewCollection(records: List<Record>): Pair<List<RecordPK>, Int> {
        database.useTransaction {
            val aRecords = batchInsertRecords(records)
            val collectionId = addRecordCollection()
            batchAssociateRecordsToCollection(aRecords, collectionId)
            return Pair(aRecords, collectionId)
        }
    }

    /**
     * Insert [record] and return [record] with a generated id.
     *
     * @param record record to insert.
     * @return Returns record with generated id or null if failed.
     */
    fun insertRecord(record: Record): RecordPK?

    /**
     * Batch insert records.
     *
     * @param records Records to insert.
     * @return Insert [records] and return [records] with a generated ids. If failed then revert changes and return null.
     * @exception CompositionException [failed to insert a record][CompositionCode.FailedToInsertRecord].
     */
    @Throws(CompositionException::class)
    fun batchInsertRecords(records: List<Record>): List<RecordPK> {
        val gson = Gson()
        database.useTransaction {
            return records.map {
                val res: RecordPK? = insertRecord(it)
                if (res == null) {
                    throw CompositionException(CompositionCode.FailedToInsertRecord, gson.toJson(it))
                } else return@map res
            }
        }
    }

    /**
     * Insert a new collection.
     *
     * @return collection id
     */
    fun addRecordCollection(): Int

    /**
     * Associate multiple records to collection
     *
     * @param records Associate record by id to collection of [collectionId].
     * @param collectionId Id of collection.
     *
     * @exception CompositionCode [No records provided][CompositionCode.NoRecordsProvided],
     * [failed to associate record to collection][CompositionCode.FailedToAssociateRecordToCollection]
     */
    @Throws(CompositionException::class)
    fun batchAssociateRecordsToCollection(records: List<RecordPK>, collectionId: Int)

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
     * @param collectionId Id of collection
     * @param records records to update to
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
}