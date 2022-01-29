package com.idealIntent.repositories.collections

import com.idealIntent.dtos.compositions.RecordUpdate

/**
 * Structure for CRUD operations on compositions' collections
 *
 * Methods of this interface is applicable only to low level specific such as ImageCollection, TextCollection and PrivilegesCollection
 *
 * @param Record Generic type for data such as Image, Text, PrivilegedAuthor, etc.
 * @param MetadataOfCollection Metadata of collection such as ImageCollection, TextCollection, PrivilegesCollection, etc.
 * @param Collection DTO collection of Records.
 * @constructor Create empty structure for collections
 */
interface ICollectionStructure<Record, MetadataOfCollection, Collection> {

    // region Get
    /**
     * Get records by collection id
     *
     * @param id Get records under collection's id
     * @return Records under [id] or null if failed to find by [id]
     */
    fun getAssortmentById(id: Int): Collection

    /**
     * Get only record collection's metadata and not it's associated records
     *
     * @param id ID of collection
     * @return Collection's metadata and not associated records or null if failed to find by [id]
     */
    fun getMetadataOfCollection(id: Int): MetadataOfCollection?
    // endregion Get


    // region Insert
    /**
     * Insert [record] under a new collection
     *
     * @param record Record to insert
     * @param label Name the collection
     * @return CollectionId or null if failed to insert [record]
     */
    fun insertNewRecord(record: Record, label: String): Int?

    /**
     * Batch insert [records] under a new collection
     *
     * @param records List of records to insert
     * @param label Name the collection
     * @return CollectionId or null if failed to insert [records]
     */
    fun batchInsertNewRecords(records: List<Record>, label: String): Int?

    /**
     * Insert record
     *
     * @param record Record to insert
     * @param id ID of collection to insert [record] under
     * @return Success or fail in insertion
     */
    fun insertRecord(record: Record, id: Int): Boolean

    /**
     * Batch insert records
     *
     * @param records
     * @param id ID to identify under what collection to insert [records]
     * @return Success or fail in inserting [records]
     */
    fun batchInsertRecords(records: List<Record>, id: Int): Boolean

    /**
     * Insert collection [label] in order to group new records under
     *
     * @param label Name the collection
     * @return CollectionId or null if failed to insert new collection of [label]
     */
    fun insertRecordCollection(label: String): Int
    // endregion Insert


    // region Update
    /**
     * Update record
     *
     * @param id ID to identify under what collection [record] is under
     * @param record Update to
     * @return Success or fail in updating [record]
     */
    fun updateRecord(record: RecordUpdate, id: Int): Boolean

    /**
     * Batch update records
     *
     * @param id ID to identify under what collection [records] are under
     * @param records Update to
     */
    fun batchUpdateRecords(records: List<RecordUpdate>, id: Int): Boolean
    // endregion Update


    // todo deletes
    // region Delete
    /**
     * Delete an image of collection
     *
     */
    fun deleteRecord(id: Int): Boolean

    /**
     * Delete images of collection
     *
     */
    fun batchDeleteRecords(id: Int): Boolean

    /**
     * Delete all images of collection
     *
     */
    fun deleteAllRecordsInCollection(id: Int)

    /**
     * Delete image_collection and it's images
     *
     */
    fun deleteCollectionOfRecords()
    // endregion Delete
}