package com.idealIntent.managers.compositions.images

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.managers.compositions.D2RepoStructure
import com.idealIntent.models.compositions.basicCollections.images.D2ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImageCollectionToD2CollectionsModel
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey

class D2ImageRepository(
    val imageRepository: ImageRepository,
) : D2RepoStructure<Image>() {

    companion object {
        val d2ImgCol = D2ImageCollectionsModel.aliased("d2ImgCol")
        val imgCol2ImgD2Col = ImageCollectionToD2CollectionsModel.aliased("imgCol2ImgD2Col")
    }

    override fun batchInsertRecordsToNewCollection(recordCollections: List<Pair<List<Image>, Int>>): Int {
        database.useTransaction {
            val idsAndOrderRanksOfCollections = recordCollections.mapIndexed { idx, textCollection ->
                Pair(imageRepository.batchInsertRecordsToNewCollection(textCollection.first), textCollection.second)
            }

            val d2CollectionId = database.insertAndGenerateKey(d2ImgCol) {} as Int

            idsAndOrderRanksOfCollections.forEach { (collectionId, orderRank) ->
                database.insert(imgCol2ImgD2Col) {
                    set(it.imageCollectionId, d2CollectionId)
                    set(it.d2ImageCollectionId, collectionId)
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