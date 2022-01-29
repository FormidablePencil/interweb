package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.dtos.compositions.RecordUpdate
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.compositions.genericStructures.texts.Text
import dtos.compositions.genericStructures.texts.TextCollection
import dtos.compositions.genericStructures.texts.TextIdentifiableRecordByCol
import dtos.compositions.genericStructures.texts.TextsCOL
import models.genericStructures.ITextCollectionSchema
import models.genericStructures.TextCollections
import models.genericStructures.Texts
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

/**
 * Responsible for text_collections and texts table
 */
class TextRepository : RepositoryBase(), ICollectionStructure<Text, ITextCollectionSchema, TextCollection> {
    private val Database.textCollections get() = this.sequenceOf(TextCollections)
    private val Database.texts get() = this.sequenceOf(Texts)

    // region Get
    override fun getAssortmentById(id: Int): TextCollection {
        val textCol = TextCollections.aliased("textCol")
        val text = Texts.aliased("text")

        var label = ""
        val texts = database.from(textCol)
            .leftJoin(text, text.collectionId eq textCol.id)
            .select(textCol.label, text.orderRank, text.orderRank, text.text)
            .where { textCol.id eq id }
            .map { row ->
                label = row[textCol.label]!!
                Text(
                    orderRank = row[text.orderRank]!!,
                    text = row[text.text]!!
                )
            }
        return TextCollection(label, texts)
    }

    override fun getMetadataOfCollection(id: Int): ITextCollectionSchema? {
        return database.textCollections.find { it.id eq id }
    }
    // endregion Get


    // region Insert
    override fun insertNewRecord(record: Text, label: String): Int? {
        val collectionId = insertRecordCollection(label)
            ?: return null

        insertRecord(record, collectionId)

        return collectionId
    }

    override fun batchInsertNewRecords(records: List<Text>, label: String): Int? {
        val collectionId = insertRecordCollection(label)
            ?: return null

        batchInsertRecords(records, collectionId)

        return collectionId
    }

    override fun insertRecord(record: Text, id: Int): Boolean {
        return database.insert(Texts) { // todo - validate idsOfTexts
            set(it.orderRank, record.orderRank)
            set(it.text, record.text)
            set(it.collectionId, id)
        } != 0
    }

    override fun batchInsertRecords(records: List<Text>, id: Int): Boolean {
        val effectedRows = database.batchInsert(Texts) { // todo - validate idsOfTexts
            records.map { text ->
                item {
                    set(it.orderRank, text.orderRank)
                    set(it.text, text.text)
                    set(it.collectionId, id)
                }
            }
        }
        effectedRows // todo - handle create all or create non here
        return false
    }

    override fun insertRecordCollection(label: String): Int {
        return database.insertAndGenerateKey(TextCollections) {
            set(it.label, label)
        } as Int? ?: TODO("no reason to fail, therefore return Int or throw ServerError and log Exception")
    }
    // endregion Insert


    // region Update
    override fun updateRecord(record: RecordUpdate, id: Int): Boolean {
        val collection = getMetadataOfCollection(id) ?: return false // todo - handle gracefully

        database.update(Texts) {
            record.updateTo.map { recordCol ->
                when (TextsCOL.fromInt(recordCol.column)) {
                    TextsCOL.Text -> set(it.text, recordCol.value)
                    TextsCOL.OrderRank -> set(it.orderRank, recordCol.value.toInt()) // todo - may fail
                    // todo - toInt() may fail
                }
            }
            where {
                when (TextIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                    TextIdentifiableRecordByCol.OrderRank ->
                        (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
                } // todo - handle incorrect recordIdentifiableByCol gracefully
                // todo - handle failure gracefully
            }
        }
        TODO("validate if successful")
    }

    override fun batchUpdateRecords(records: List<RecordUpdate>, id: Int): Boolean {
        val collection = getMetadataOfCollection(id) ?: return false // todo - hadle gracefully

        database.batchUpdate(Texts) {
            records.map { record ->
                item {
                    record.updateTo.map { updateCol ->
                        when (TextsCOL.fromInt(updateCol.column)) {
                            TextsCOL.Text -> set(it.text, updateCol.value)
                            TextsCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
                            // todo - toInt() may fail, handle gracefully
                        }
                    }
                    where {
                        when (TextIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                            TextIdentifiableRecordByCol.OrderRank ->
                                (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
                        } // todo - handle incorrect recordIdentifiableByCol gracefully
                    }
                }
            }
        }
        TODO("validate if successful")
    }
    // endregion Update


    // region Delete
    override fun deleteRecord(id: Int): Boolean {

        TODO("Not yet implemented")
    }

    override fun batchDeleteRecords(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteAllRecordsInCollection(id: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionOfRecords() {
        TODO("Not yet implemented")
    }
    // endregion Delete
}
