package com.idealIntent.managers.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.managers.compositions.D2RepoStructure
import com.idealIntent.models.compositions.basicCollections.texts.D2TextCollectionModel
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionToD2CollectionsModel
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey

class D2TextRepository(
    val textRepository: TextRepository,
) : D2RepoStructure<Text>() {

    companion object {
        val d2TextCol = D2TextCollectionModel.aliased("d2TextCol")
        val textCol2TextD2Col = TextCollectionToD2CollectionsModel.aliased("textCol2TextD2Col")
    }

    override fun batchInsertRecordsToNewCollection(recordCollections: List<Pair<Int, List<Text>>>): Int {
        database.useTransaction {
            val listOfOrderRankAndIdOfCollection = recordCollections.mapIndexed { idx, textCollection ->
                Pair(textCollection.first, textRepository.batchInsertRecordsToNewCollection(textCollection.second))
            }

            val d2CollectionId = database.insertAndGenerateKey(d2TextCol) {} as Int

            listOfOrderRankAndIdOfCollection.forEach { (orderRank, collectionId) ->
                database.insert(textCol2TextD2Col) {
                    set(it.collectionId, collectionId)
                    set(it.d2CollectionId, d2CollectionId)
                    set(it.orderRank, orderRank)
                }
            }

            return d2CollectionId
        }
    }

    override fun deleteRecordsCollection(d2RecordCollectionId: Int) {
        database.useTransaction {
            // get all collection ids then recordRepository.delete
            // then delete 2dcollection
        }
    }
}