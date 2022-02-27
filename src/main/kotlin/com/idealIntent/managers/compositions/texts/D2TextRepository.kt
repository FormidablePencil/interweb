package com.idealIntent.managers.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.managers.compositions.D2RepoStructure
import com.idealIntent.models.compositions.basicCollections.images.D2ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionToD2CollectionsModel
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey

class D2TextRepository(
    val textRepository: TextRepository,
) : D2RepoStructure<Text>() {

    companion object {
        val d2TextCol = D2ImageCollectionsModel.aliased("d2TextCol")
        val textCol2TextD2Col = TextCollectionToD2CollectionsModel.aliased("textCol2TextD2Col")
    }

    override fun batchInsertRecordsToNewCollection(recordCollections: List<Pair<List<Text>, Int>>): Int {
        database.useTransaction {
            val idsAndOrderRanksOfCollections = recordCollections.mapIndexed { idx, textCollection ->
                Pair(textRepository.batchInsertRecordsToNewCollection(textCollection.first), textCollection.second)
            }

            val d2CollectionId = database.insertAndGenerateKey(d2TextCol) {} as Int

            idsAndOrderRanksOfCollections.forEach { (collectionId, orderRank) ->
                database.insert(textCol2TextD2Col) {
                    set(it.imageCollectionId, d2CollectionId)
                    set(it.d2ImageCollectionId, collectionId)
                    set(it.orderRank, orderRank)
                }
            }

            return d2CollectionId
        }
    }

    override fun deleteRecordsCollection(d2RecordCollectionId: Int) {

    }
}