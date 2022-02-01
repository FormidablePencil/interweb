package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImageToCollection
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class ImageRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    lateinit var imageRepository: ImageRepository

    init {
        beforeEach {
            clearAllMocks()
            imageRepository = ImageRepository()
        }

        val images = listOf(
            Image(
                id = null,
                orderRank = 10000,
                description = "PAO Mnemonic System",
                url = " https://i.ibb.co/1K7jQJw/pao.png"
            ),
            Image(
                id = null,
                orderRank = 20000,
                description = "Emoji Tack Toes",
                url = "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
            ),
            Image(
                id = null,
                orderRank = 30000,
                description = "Crackalackin",
                url = "https://i.ibb.co/4YP7yDb/crackalackin.png"
            ),
            Image(
                id = null,
                orderRank = 40000,
                description = "Pokedex",
                url = "https://i.ibb.co/w0m4pF3/pokedex.png"
            ),
        )

        // region Insert
        given("insertRecord") {
            And("addRecordCollection") {
                And("createRecordToCollectionRelationship") {
                    then("getRecordOfCollection") {
                        rollback {
                            val resImage = imageRepository.insertRecord(images[0])
                                ?: throw Exception("failed to insert image")
                            val collectionId = imageRepository.addRecordCollection()
                            val hasCreatedRelations = imageRepository.createRecordToCollectionRelationship(
                                ImageToCollection(
                                    imageId = resImage.id!!,
                                    collectionId = collectionId,
                                    orderRank = resImage.orderRank
                                )
                            )
                            hasCreatedRelations shouldBe true
                            imageRepository.getRecordOfCollection(resImage.id!!, collectionId)
                        }
                    }
                }
            }
        }

        given("batchInsertRecords") {
            And("addRecordCollection") {
                And("createRecordToCollectionRelationship") {
                    then("getCollectionOfRecords") {
                        rollback {
                            val resImages = imageRepository.batchInsertRecords(images)
                            resImages.map { it.id shouldNotBe null }

                            val collectionId = imageRepository.addRecordCollection()

                            val hasCreatedRelations =
                                imageRepository.batchCreateRecordToCollectionRelationship(resImages, collectionId)
                            hasCreatedRelations shouldBe true

                            val res = imageRepository.getCollectionOfRecords(collectionId)

                            res shouldNotBe null
                            print(res.images)
                            res.images.size shouldBe images.size // todo - test fails because more than was given is returned
                            res.images.map {
                                images.find { image ->
                                    image.orderRank == it.orderRank
                                            && image.description == it.description
                                            && image.url == it.url
                                } ?: throw Exception("failed to find returned image")
                            }
                        }
                    }
                }
            }
        }
        // endregion Insert

        xgiven("updateRecord") {
            then("getComposition") {}
        }

        xgiven("batchUpdateRecords") {
            then("getComposition") {}
        }
    }
}
