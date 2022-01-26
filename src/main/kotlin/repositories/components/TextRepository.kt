package repositories.components

import dtos.libOfComps.genericStructures.IText
import dtos.libOfComps.genericStructures.TextCollection
import models.genericStructures.TextCollections
import models.genericStructures.Texts
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class TextRepository : RepositoryBase() {
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

fun getCollectionOfTextsById(): TextCollection {
        val textCol = TextCollections.aliased("textCol") // Line 3, give an alias to the Employees table.
        val textCol2 = TextCollections.aliased("textCol2") // Line 4, give another alias to the Employees table.
        val text = Texts.aliased("img")

        var collectionOf = ""

        val images = database.from(textCol)
            .leftJoin(textCol2, on = textCol.id eq textCol2.id)
            .leftJoin(text, on = textCol.id eq text.collectionId)
            .select(text.text, text.orderRank, textCol.collectionOf)
            .map { row ->
                collectionOf = row[textCol.collectionOf]!!

                object : IText {
                    override val text = row[text.text]!! // todo - may fail
                    override val orderRank = row[text.orderRank]!!
                }
            }
        return TextCollection(collectionOf, images)
    }
}