package com.idealIntent.repositories.components

import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.serialized.libOfComps.RecordUpdate
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

/**
 * Responsible for text_collections and texts table
 */
class TextRepository : RepositoryBase() {
    private val Database.textCollections get() = this.sequenceOf(TextCollections)
    private val Database.texts get() = this.sequenceOf(Texts)

    /**
     * Get items by collection id
     *
     * @param collectionId
     * @return items by collection id
     */
    fun getAssortmentById(collectionId: Int): TextCollection {
        val textCol = TextCollections.aliased("textCol")
        val text = Texts.aliased("text")

        var collectionOf = ""
        val texts = database.from(textCol)
            .leftJoin(text, text.collectionId eq textCol.id)
            .select(textCol.collectionOf, text.orderRank, text.orderRank, text.text)
            .where { textCol.id eq collectionId }
            .map { row ->
                collectionOf = row[textCol.collectionOf]!!
                Text(
                    orderRank = row[text.orderRank]!!,
                    text = row[text.text]!!
                )
            }
        return TextCollection(collectionOf, texts)
    }

    /**
     * Insert item under a new collection
     *
     * @param text
     * @param collectionOf name the collection
     * @return collectionId or null if failed
     */
    fun insertNewText(text: Text, collectionOf: String): Int? {
        val collectionId = insertTextCollection(collectionOf)
            ?: return null

        insertText(text, collectionId)

        return collectionId
    }

    /**
     * Batch insert items under a new collection
     *
     * @param texts
     * @param collectionOf name the collection
     * @return collectionId or null if failed
     */
    fun batchInsertNewTexts(texts: List<Text>, collectionOf: String): Int? {
        val collectionId = insertTextCollection(collectionOf)
            ?: return null

        batchInsertTexts(texts, collectionId)

        return collectionId
    }

    /**
     * Insert item
     *
     * @param text
     * @param collectionId id of collection to associate to
     * @return success or fail in creation
     */
    fun insertText(text: Text, collectionId: Int): Boolean {
        return database.insert(Texts) { // todo - validate idsOfTexts
            set(it.orderRank, text.orderRank)
            set(it.text, text.text)
            set(it.collectionId, collectionId)
        } != 0
    }

    /**
     * Batch insert items
     *
     * @param texts
     * @param collectionId id to identify under what collection to insert
     * @return success or fail in creation
     */
    fun batchInsertTexts(texts: List<Text>, collectionId: Int): Boolean {
        val effectedRows = database.batchInsert(Texts) { // todo - validate idsOfTexts
            texts.map { text ->
                item {
                    set(it.orderRank, text.orderRank)
                    set(it.text, text.text)
                    set(it.collectionId, collectionId)
                }
            }
        }
        effectedRows // todo - handle create all or create non here
        return false
    }

    /**
     * Insert collection in order to group new items under
     *
     * @param collectionOf name the collection
     * @return collectionId or null if failed
     */
    private fun insertTextCollection(collectionOf: String): Int? {
        return database.insertAndGenerateKey(TextCollections) {
            set(it.collectionOf, collectionOf)
        } as Int?
    }


    /**
     * Update item
     *
     * @param collectionId
     * @param record update to
     */
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

    /**
     * Batch update items
     *
     * @param collectionId
     * @param records update to
     */
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


    /**
     * Get only text_collection and not it's associated items
     *
     * @param id id of collection
     * @return image_collection but not associated items
     */
    private fun getTextCollection(id: Int): ITextCollectionSchema? {
        return database.textCollections.find { it.id eq id }
    }
}
