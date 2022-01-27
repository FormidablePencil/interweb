package repositories.components

import dtos.libOfComps.genericStructures.IText
import dtos.libOfComps.genericStructures.Text
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

    fun getCollectionById(id: Int): TextCollection {
        val textCol = TextCollections.aliased("textCol")
        val text = Texts.aliased("text")

        var collectionOf = ""
        val texts = database.from(textCol)
            .leftJoin(text, text.collectionId eq textCol.id)
            .select(textCol.collectionOf, text.orderRank, text.orderRank, text.text)
            .where { textCol.id eq id }
            .map { row ->
                collectionOf = row[textCol.collectionOf]!!
                Text(
                    orderRank = row[text.orderRank]!!,
                    text = row[text.text]!!
                )
            }
        return TextCollection(collectionOf, texts)
    }
}