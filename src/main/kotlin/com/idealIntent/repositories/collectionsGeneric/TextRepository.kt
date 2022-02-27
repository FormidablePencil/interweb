package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextCollection
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.collectionsGeneric.texts.TextToCollection
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositions.basicCollections.texts.ITextToCollectionEntity
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.collectionsGeneric.texts.TextsCOL
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

/**
 * Responsible for collections of texts.
 *
 * Overriding functions that call the inherited functions from [ICollectionStructure] are there for structure.
 * Having them in this way makes for easier unit testing.
 *
 * Responsible for -
 * [text][models.compositions.basicsCollections.texts.IText],
 * [text to collections][models.compositions.basicsCollections.texts.ITextToCollection],
 * [text collection][models.compositions.basicsCollections.texts.ITextCollection].
 */
class TextRepository : RepositoryBase(),
    ICollectionStructure<Text, TextPK, ITextToCollectionEntity, TextToCollection, TextCollection> {
    private val Database.textCollections get() = this.sequenceOf(TextCollectionsModel)
    private val Database.textToCollections get() = this.sequenceOf(TextToCollectionsModel)
    private val Database.texts get() = this.sequenceOf(TextsModel)

    companion object {
        val text2Col = TextToCollectionsModel.aliased("textRedirect2Col")
        val text = TextsModel.aliased("textRedirect")
    }


    // region Get records
    override fun getSingleRecordOfCollection(recordId: Int, collectionId: Int): TextPK? =
        getRecordsQuery(recordId, collectionId)?.first()

    override fun getAllRecordsOfCollection(collectionId: Int): List<TextPK>? =
        getRecordsQuery(null, collectionId)

    private fun getRecordsQuery(recordId: Int?, collectionId: Int): List<TextPK>? {
        val itemToCol = TextToCollectionsModel.aliased("textToCol")
        val item = TextsModel.aliased("text")

        val records = database.from(itemToCol)
            .leftJoin(item, item.id eq itemToCol.textId)
            .select(itemToCol.textId, itemToCol.orderRank, item.text)
            .whereWithConditions {
                it += (item.id eq itemToCol.textId) and (itemToCol.collectionId eq collectionId)
                if (recordId != null) it += (item.id eq recordId)
            }
            .map { row ->
                TextPK(
                    id = row[itemToCol.textId]!!,
                    orderRank = row[itemToCol.orderRank]!!,
                    text = row[item.text]!!,
                )
            }

        return records.ifEmpty { null }
    }
    // endregion


    override fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean =
        database.textToCollections.find { (it.textId eq recordId) and (it.collectionId eq collectionId) } != null


    // region Insert
    override fun batchInsertRecordsToNewCollection(records: List<Text>): Int {
        val collectionId = addRecordCollection()
        batchInsertRecordsToCollection(records, collectionId)
        return collectionId
    }

    override fun batchInsertRecordsToCollection(records: List<Text>, collectionId: Int): Boolean {
        records.forEach {
            if (!insertRecordToCollection(it, collectionId))
                return@batchInsertRecordsToCollection false
        }
        return true
    }

    override fun insertRecordToNewCollection(record: Text): Int {
        val collectionId = addRecordCollection()
        insertRecordToCollection(record, collectionId)
        return collectionId
    }

    override fun insertRecordToCollection(record: Text, collectionId: Int): Boolean {
        val id = database.insertAndGenerateKey(TextsModel) {
            set(it.text, record.text)
        } as Int
        return associateRecordToCollection(orderRank = record.orderRank, recordId = id, collectionId = collectionId)
    }

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(TextCollectionsModel) { } as Int

    override fun batchAssociateRecordsToCollection(records: List<TextPK>, collectionId: Int) {
        database.useTransaction {
            if (records.isEmpty()) throw CompositionException(CompositionCode.EmptyListOfRecordsProvided)
            records.forEach {
                try {
                    associateRecordToCollection(
                        orderRank = it.orderRank,
                        collectionId = collectionId,
                        recordId = it.id
                    )
                } catch (ex: Exception) {
                    throw CompositionException(
                        CompositionCode.FailedToAssociateRecordToCollection, it.orderRank.toString(), ex
                    )
                }
            }
        }
    }

    override fun associateRecordToCollection(orderRank: Int, recordId: Int, collectionId: Int): Boolean =
        database.insert(TextToCollectionsModel) {
            set(it.collectionId, collectionId)
            set(it.textId, recordId)
            set(it.orderRank, orderRank)
        } == 1
    // endregion Insert


    // region Update record
    override fun updateRecord(record: RecordUpdate) {
        database.useTransaction {
            record.updateTo.map { updateCol ->
                when (TextsCOL.fromInt(updateCol.column)) {
                    TextsCOL.Text -> updateRecordOnly(TextsCOL.Text, updateCol.value, record.recordId)
                    TextsCOL.OrderRank -> {
                        try {
                            val orderRank = updateCol.value.toInt()
                            updateOrderRank(orderRank, record.recordId)
                        } catch (ex: NumberFormatException) {
                            throw CompositionException(
                                CompositionCode.ProvidedStringInPlaceOfInt,
                                "On column: ${TextsCOL.OrderRank.name}. Record id: ${record.recordId}. Value provided: ${updateCol.value}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateRecordOnly(column: TextsCOL, newValue: String, recordId: Int) {
        if (database.update(TextsModel) {
                when (column) {
                    TextsCOL.Text ->
                        set(it.text, newValue)
                    else ->
                        throw CompositionExceptionReport(CompositionCode.ColumnDoesNotExist, this::class.java)
                }
                where { text.id eq recordId }
            } == 0) throw CompositionExceptionReport(
            CompositionCode.FailedToAddRecordToCompositionValidator, this::class.java
        )
    }

    private fun updateOrderRank(orderRank: Int, recordId: Int) {
        if (database.update(TextToCollectionsModel) {
                set(it.orderRank, orderRank)
                where { it.textId eq recordId }
            } == 0) throw CompositionExceptionReport(
            CompositionCode.FailedToAddRecordToCompositionValidator, this::class.java
        )
    }
    // endregion Update record


    // region Delete
    override fun deleteRecord(recordId: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteRecordsCollection(collectionId: Int) {
        database.useTransaction {
            val record = getAllRecordsOfCollection(collectionId)
                ?: throw CompositionException(CompositionCode.CollectionOfRecordsNotFound)

            database.delete(TextToCollectionsModel) { it.collectionId eq collectionId }
            database.delete(TextCollectionsModel) { it.id eq collectionId }

            record.forEach { item ->
                database.delete(TextsModel) { it.id eq item.id }
            }
        }
    }

    override fun disassociateRecordFromCollection(recordId: Int, collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionButNotRecord() {
        TODO("Not yet implemented")
    }
// endregion Delete
}
