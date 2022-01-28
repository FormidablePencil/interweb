package com.idealIntent.repositories.components

import com.idealIntent.exceptions.ServerErrorException
import dtos.libOfComps.genericStructures.texts.Text
import dtos.libOfComps.genericStructures.texts.TextCollection
import dtos.libOfComps.genericStructures.texts.TextIdentifiableRecordByCol
import dtos.libOfComps.genericStructures.texts.TextsCOL
import models.genericStructures.ITextCollectionSchema
import models.genericStructures.TextCollections
import models.genericStructures.Texts
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.serialized.libOfComps.RecordUpdate

class TextRepository : RepositoryBase() {
    private val Database.textCollections get() = this.sequenceOf(TextCollections)
    private val Database.texts get() = this.sequenceOf(Texts)

    fun insertNewText(collectionOf: String) {
        val collectionId = insertTextCollection(collectionOf)

        insertText(collectionId)
    }

    fun batchInsertNewText(texts: List<Text>, collectionOf: String): Int {
        val collectionId = insertTextCollection(collectionOf)

        batchInsertTexts(texts, collectionId)

        return collectionId
    }


    fun insertText(collectionId: Int) {

    }

    fun batchInsertTexts(texts: List<Text>, collectionId: Int): IntArray {
        val idsOfTexts = database.batchInsert(Texts) { // todo - validate idsOfTexts
            texts.map { text ->
                item {
                    set(it.orderRank, text.orderRank)
                    set(it.text, text.text)
                    set(it.collectionId, collectionId)
                }
            }
        }
        return idsOfTexts // todo - handle create all or create non here
    }

    fun insertTextCollection(collectionOf: String): Int {
        val id = database.insertAndGenerateKey(TextCollections) {
            set(it.collectionOf, collectionOf)
        } as Int?

//        return id ?: throw ServerErrorException()
        TODO("handle failure to create id here. Log the error")
    }

    fun getAssortmentById(textCollectionId: Int): TextCollection {
        val textCol = TextCollections.aliased("textCol")
        val text = Texts.aliased("text")

        var collectionOf = ""
        val texts = database.from(textCol)
            .leftJoin(text, text.collectionId eq textCol.id)
            .select(textCol.collectionOf, text.orderRank, text.orderRank, text.text)
            .where { textCol.id eq textCollectionId }
            .map { row ->
                collectionOf = row[textCol.collectionOf]!!
                Text(
                    orderRank = row[text.orderRank]!!,
                    text = row[text.text]!!
                )
            }
        return TextCollection(collectionOf, texts)
    }

    fun updateText(collectionId: Int, record: RecordUpdate) {
        val collection = getTextCollection(collectionId) ?: return // todo - handle gracefully

        database.update(Texts) {
            record.updateRecord.map { updateCol ->
                when (TextsCOL.fromInt(updateCol.column)) {
                    TextsCOL.Text -> set(it.text, updateCol.value)
                    TextsCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt()) // todo - may fail
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
    }

    fun batchUpdateTexts(collectionId: Int, records: List<RecordUpdate>) {
        val collection = getTextCollection(collectionId) ?: return // todo - handle gracefully

        database.batchUpdate(Texts) {
            records.map { record ->
                item {
                    record.updateRecord.map { updateCol ->
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
    }

    private fun getTextCollection(id: Int): ITextCollectionSchema? {
        return database.textCollections.find { it.id eq id }
    }
}
