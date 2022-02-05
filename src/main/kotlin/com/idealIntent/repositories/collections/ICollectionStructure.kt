package com.idealIntent.repositories.collections

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import org.ktorm.database.Database

// todo - rename collection to collection_source

/**
 * Structure for CRUD operations on compositions' collections
 *
 * Methods of this interface is applicable only to low level specific such as ImageCollection, TextCollection and PrivilegesCollection
 *
 * [Record] has no primary key [RecordPK], out has a primary key.
 *
 * Records cannot be standalone, they must be associated to a collection.
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
    fun getSingleRecordOfCollection(recordId: Int, collectionId: Int): RecordPK?

    /**
     * Get records by collection id.
     *
     * @param collectionId Get records under collection's id
     * @return A list of records under collection of [collectionId] or null if non found
     */
    fun getAllRecordsOfCollection(collectionId: Int): List<RecordPK>?

    /**
     * Get records query. Not used directly but by [getSingleRecordOfCollection] and [getAllRecordsOfCollection]
     *
     * @param recordId Id of record to look up null for returning only record
     * @param collectionId Get records under collection's id
     * @return A list of records under collection of [collectionId] or null if non found
     */
    fun getRecordsQuery(recordId: Int? = null, collectionId: Int): List<RecordPK>?

    /**
     * validate image to collection relationship.
     */
    // todo - no use for it yet. Maybe deleted
    fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean
    // endregion Get


    // region Insert
    /**
     * Batch insert records to new collection
     *
     * @param records Records to insert.
     * @return collectionId. Should never fail because it's creating a new collection and inserting new records.
     */
    fun batchInsertRecordsToNewCollection(records: List<Record>): Int?

    /**
     * Insert [record] and return [record] with a generated id.
     *
     * @param record record to insert.
     * @return Returns record with generated id.
     */
    fun insertRecordToCollection(record: Record, collectionId: Int): Boolean

    /**
     * Batch insert records.
     *
     * @param records Records to insert.
     * @return Insert [records] and return [records] with a generated ids. If failed then revert changes and return null.
     */
    fun batchInsertRecordsToCollection(records: List<Record>, collectionId: Int): Boolean

    /**
     * Insert record to new collection.
     *
     * @return collection id
     */
    fun insertRecordToNewCollection(record: Record): Int

    /**
     * Add a new collection for records
     *
     * @return id of collection
     */
    fun addRecordCollection(): Int

    /**
     * Associate multiple records to collection. Used for when you want records to belong to multiple collections.
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
    fun associateRecordToCollection(orderRank: Int, recordId: Int, collectionId: Int): Boolean
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