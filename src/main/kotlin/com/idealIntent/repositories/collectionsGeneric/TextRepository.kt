package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextCollection
import com.idealIntent.dtos.collectionsGeneric.texts.TextToCollection
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionCode.ServerError
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.exceptions.TempException
import com.idealIntent.models.compositions.basicCollections.texts.ITextToCollectionEntity
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.collectionsGeneric.texts.TextIdentifiableRecordByCol
import dtos.collectionsGeneric.texts.TextsCOL
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

/**
 * Responsible for collections of texts.
 *
 * Related -
 * [text][models.compositions.basicsCollections.texts.IText],
 * [text to collections][models.compositions.basicsCollections.texts.ITextToCollection],
 * [text collection][models.compositions.basicsCollections.texts.ITextCollection].
 */
class TextRepository : RepositoryBase(),
    ICollectionStructure<Text, ITextToCollectionEntity, TextToCollection, TextCollection> {
    private val Database.textCollections get() = this.sequenceOf(TextCollectionsModel)
    private val Database.textToCollections get() = this.sequenceOf(TextToCollectionsModel)
    private val Database.texts get() = this.sequenceOf(TextsModel)

    // region Get
    override fun getRecordsQuery(recordId: Int?, collectionId: Int): List<Text> {
        val itemToCol = TextToCollectionsModel.aliased("textToCol")
        val item = TextsModel.aliased("text")

        val images = database.from(itemToCol)
            .leftJoin(item, item.id eq itemToCol.textId)
            .select(itemToCol.textId, itemToCol.orderRank, item.text)
            .whereWithConditions {
                (item.id eq itemToCol.textId) and (itemToCol.collectionId eq collectionId)
                if (recordId != null) it += (item.id eq recordId)
            }
            .map { row ->
                Text(
                    id = row[itemToCol.textId],
                    orderRank = row[itemToCol.orderRank]!!,
                    text = row[item.text]!!,
                )
            }
        return images
    }

    override fun getRecordToCollectionInfo(recordId: Int, collectionId: Int): ITextToCollectionEntity? =
        database.textToCollections.find { (it.textId eq recordId) and (it.collectionId eq collectionId) }
    // endregion Get


    // region Insert
    override fun insertRecord(record: Text): Text? {
        val id = database.insertAndGenerateKey(TextsModel) {
            set(it.text, record.text)
        } as Int? ?: return null
        record.id = id
        return record
    }

    override fun batchInsertRecords(records: List<Text>): List<Text> =
        records.map {
            return@map insertRecord(it)
                ?: TODO("throw exception of one fails. Either one inserts or non do.")
        }

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(TextCollectionsModel) { } as Int?
            ?: throw TempException("failed to create ImageCollection (should always succeed)", this::class.java)

    @Throws(CompositionException::class, CompositionExceptionReport::class)
    override fun batchAssociateRecordsToCollection(records: List<Text>, collectionId: Int) {
        try {
            database.useTransaction {
                records.map {
                    if (it.id == null) TODO("terminate batch completely")
//                    throw CompositionExceptionReport(CompositionCode.InvalidIdOfRecord, this::class.java)
                    val succeed = associateRecordToCollection(
                        TextToCollection(
                            orderRank = it.orderRank,
                            collectionId = collectionId,
                            textId = it.id!!
                        )
                    )
                    if (!succeed) TODO("terminate batch completely")
//                    throw CompositionExceptionReport(CompositionCode.FailedToInsertRecord, this::class.java)
                }
            }
        } catch (ex: CompositionException) {
            when (ex.code) {
                // todo
                else -> throw CompositionExceptionReport(ServerError, this::class.java, ex)
            }
        }
    }

    override fun associateRecordToCollection(recordToCollection: TextToCollection): Boolean =
        database.insert(TextToCollectionsModel) {
            set(it.collectionId, recordToCollection.collectionId)
            set(it.textId, recordToCollection.textId)
            set(it.orderRank, recordToCollection.orderRank)
        } != 0
    // endregion Insert


    // region Update
    override fun updateRecord(record: RecordUpdate, imageId: Int, collectionId: Int): Boolean {
//        val collection =
//            validateRecordToCollectionRelationship(collectionId) ?: return false // todo - handle gracefully

        database.update(TextsModel) {
            record.updateTo.map { recordCol ->
                when (TextsCOL.fromInt(recordCol.column)) {
                    TextsCOL.Text -> set(it.text, recordCol.value)
//                    TextsCOL.OrderRank -> set(it.orderRank, recordCol.value.toInt()) // todo - may fail
                    // todo - toInt() may fail
                }
            }
            where {
                when (TextIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                    TextIdentifiableRecordByCol.OrderRank ->
                        TODO()
//                        (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
                } // todo - handle incorrect recordIdentifiableByCol gracefully
                // todo - handle failure gracefully
            }
        }
        TODO("validate if successful")
    }

    override fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean {
//        val collection = validateRecordToCollectionRelationship(collectionId) ?: return false // todo - hadle gracefully

        database.batchUpdate(TextsModel) {
            records.map { record ->
                item {
                    record.updateTo.map { updateCol ->
                        when (TextsCOL.fromInt(updateCol.column)) {
                            TextsCOL.Text -> set(it.text, updateCol.value)
//                            TextsCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
                            // todo - toInt() may fail, handle gracefully
                        }
                    }
                    where {
                        when (TextIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                            TextIdentifiableRecordByCol.OrderRank ->
                                TODO()
//                                (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
                        } // todo - handle incorrect recordIdentifiableByCol gracefully
                    }
                }
            }
        }
        TODO("validate if successful")
    }
    // endregion Update


    // region Delete
    override fun deleteRecord(recordId: Int, collectionId: Int): Boolean {

        TODO("Not yet implemented")
    }

    override fun batchDeleteRecords(id: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteAllRecordsInCollection(collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun disassociateRecordFromCollection(recordId: Int, collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionButNotRecord() {
        TODO("Not yet implemented")
    }
    // endregion Delete
}
