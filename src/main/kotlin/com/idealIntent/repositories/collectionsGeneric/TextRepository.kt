package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextCollection
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.collectionsGeneric.texts.TextToCollection
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
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
    ICollectionStructure<Text, TextPK, ITextToCollectionEntity, TextToCollection, TextCollection> {
    private val Database.textCollections get() = this.sequenceOf(TextCollectionsModel)
    private val Database.textToCollections get() = this.sequenceOf(TextToCollectionsModel)
    private val Database.texts get() = this.sequenceOf(TextsModel)

    // region Get
    override fun getRecordOfCollection(recordId: Int, collectionId: Int): TextPK? =
        super.getRecordOfCollection(recordId, collectionId)

    override fun getCollectionOfRecords(collectionId: Int): List<TextPK>? = super.getCollectionOfRecords(collectionId)

    override fun getRecordsQuery(recordId: Int?, collectionId: Int): List<TextPK>? {
        val itemToCol = TextToCollectionsModel.aliased("textToCol")
        val item = TextsModel.aliased("text")

        val records = database.from(itemToCol)
            .leftJoin(item, item.id eq itemToCol.textId)
            .select(itemToCol.textId, itemToCol.orderRank, item.text)
            .whereWithConditions {
                (item.id eq itemToCol.textId) and (itemToCol.collectionId eq collectionId)
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

    override fun getRecordToCollectionRelationship(recordId: Int, collectionId: Int): ITextToCollectionEntity? =
        database.textToCollections.find { (it.textId eq recordId) and (it.collectionId eq collectionId) }

    override fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean =
        super.validateRecordToCollectionRelationship(recordId, collectionId)
    // endregion Get


    // region Insert
    override fun batchInsertRecordsToNewCollection(records: List<Text>): Pair<List<TextPK>, Int> =
        super.batchInsertRecordsToNewCollection(records)

    override fun insertRecord(record: Text): TextPK? {
        val id = database.insertAndGenerateKey(TextsModel) {
            set(it.text, record.text)
        } as Int? ?: return null
        return TextPK(id = id, orderRank = record.orderRank, text = record.text)
    }

    override fun batchInsertRecords(records: List<Text>): List<TextPK> = super.batchInsertRecords(records)

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(TextCollectionsModel) { } as Int

    override fun batchAssociateRecordsToCollection(records: List<TextPK>, collectionId: Int) {
        database.useTransaction {
            if (records.isEmpty()) throw CompositionException(CompositionCode.NoRecordsProvided)
            records.forEach {
                val succeed = associateRecordToCollection(
                    TextToCollection(
                        orderRank = it.orderRank,
                        collectionId = collectionId,
                        textId = it.id
                    )
                )
                if (!succeed) throw CompositionException(CompositionCode.FailedToAssociateRecordToCollection)
            }
        }
    }

    override fun associateRecordToCollection(recordToCollection: TextToCollection): Boolean =
        database.insert(TextToCollectionsModel) {
            set(it.collectionId, recordToCollection.collectionId)
            set(it.textId, recordToCollection.textId)
            set(it.orderRank, recordToCollection.orderRank)
        } == 1
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
