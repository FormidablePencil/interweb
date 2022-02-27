package com.idealIntent.managers.compositions

import com.idealIntent.repositories.RepositoryBase

abstract class D2RepoStructure<Record> : RepositoryBase(), ID2RepoStructure<Record> {
}

private interface ID2RepoStructure<Record> {
    /**
     * Compose collection of images.
     *
     * Save collection of images, get the ids of the collections saved and associate them to the 2 dimensional compositions.
     *
     * @param recordCollections Collections of records and orderRank of collections.
     * @return Id of the 2 dimensional composition which consists of record collections.
     */
    fun batchInsertRecordsToNewCollection(recordCollections: List<Pair<Int, List<Record>>>): Int

    /**
     * Delete records of collections of 2 dimensional composition of provided [d2RecordCollectionId].
     */
    fun deleteRecordsCollection(d2RecordCollectionId: Int)
}