package repositories.components

import dtos.libOfComps.genericStructures.IText
import dtos.libOfComps.genericStructures.Text
import dtos.libOfComps.genericStructures.TextCollection
import models.genericStructures.ITextCollectionSchema
import models.genericStructures.TextCollections
import models.genericStructures.Texts
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class TextRepository : RepositoryBase() {
    private val Database.textCollections get() = this.sequenceOf(TextCollections)
    private val Database.texts get() = this.sequenceOf(Texts)

    fun insertCollectionOfTexts(texts: List<IText>, collectionOf: String): Int? {
        val navToTextCollectionId = database.insertAndGenerateKey(TextCollections) {
            set(it.collectionOf, collectionOf)
        } as Int?

        val idsOfTexts = database.batchInsert(Texts) { // todo - validate idsOfTexts
            texts.map { navToItem ->
                item {
                    set(it.orderRank, navToItem.orderRank)
                    set(it.text, navToItem.text)
                    set(it.collectionId, navToTextCollectionId)
                }
            }
        }
        return navToTextCollectionId
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

enum class TextsCOL(private val value: Int) {
    Text(0), OrderRank(1);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

enum class TextIdentifiableRecordByCol(private val value: Int) {
    OrderRank(0);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}