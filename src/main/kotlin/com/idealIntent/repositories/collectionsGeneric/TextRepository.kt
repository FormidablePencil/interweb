package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.compositions.RecordUpdate
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.collectionsGeneric.texts.*
import models.compositions.basicsCollections.texts.ITextToCollectionEntity
import models.compositions.basicsCollections.texts.TextCollectionsModel
import models.compositions.basicsCollections.texts.TextToCollectionsModel
import models.compositions.basicsCollections.texts.TextsModel
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
    override fun getCollectionOfRecords(recordId: Int, collectionId: Int): TextCollection {
        val itemCol = TextCollectionsModel.aliased("textCol")
        val itemToCol = TextToCollectionsModel.aliased("textToCol")
        val item = TextsModel.aliased("text")

        var label = ""
        val texts = database.from(itemToCol)
            .select(item.text, itemToCol.orderRank)
            .where { (itemCol.id eq recordId) and (itemToCol.collectionId eq collectionId) }
            .map { row ->
                Text(
                    orderRank = row[itemToCol.orderRank]!!,
                    text = row[item.text]!!
                )
            }
        return TextCollection(label, texts)
    }

    override fun getRecordsToCollectionInfo(recordId: Int, collectionId: Int): ITextToCollectionEntity? =
        database.textToCollections.find { (it.textId eq recordId) and (it.collectionId eq collectionId) }

    // endregion Get


    // region Insert
    override fun insertNewRecord(record: Text): TextToCollection? {
        TODO()
//        val collectionId = insertRecordCollection(record)
//            ?: return null
//
//        insertRecord(record, collectionId)
//
//        return collectionId
    }

    override fun batchInsertNewRecords(records: List<Text>): List<TextToCollection>? {
        TODO()
        val collectionId = addRecordCollection()
            ?: return null

        batchInsertRecords(records, collectionId)
    }

    override fun insertRecord(record: Text, collectionId: Int): TextToCollection? {
        TODO()
        database.insert(TextsModel) { // todo - validate idsOfTexts
//            set(it.orderRank, record.orderRank)
            set(it.text, record.text)
//            set(it.collectionId, collectionId)
        } != 0
    }

    override fun batchInsertRecords(records: List<Text>, collectionId: Int): List<TextToCollection>? {
        TODO()
        val effectedRows = database.batchInsert(TextsModel) { // todo - validate idsOfTexts
            records.map { text ->
                item {
//                    set(it.orderRank, text.orderRank)
                    set(it.text, text.text)
//                    set(it.collectionId, collectionId)
                }
            }
        }
        effectedRows // todo - handle create all or create non here

    }

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(TextCollectionsModel) { } as Int?
            ?: TODO("no reason to fail, therefore return Int or throw ServerError and log Exception")
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
