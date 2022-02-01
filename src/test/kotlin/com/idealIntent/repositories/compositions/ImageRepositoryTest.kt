package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImageToCollection
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class ImageRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var imageRepository: ImageRepository
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

        beforeEach {
            imageRepository = ImageRepository()
        }

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
                            imageRepository.getRecordOfCollection(resImage.id!!, resImage.id!!)
                        }
                    }
                }
            }
        }

        given("batchInsertRecords") {
            then("addRecordCollection") {
                then("createRecordToCollectionRelationship") {
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
                            res.images.size shouldBe images.size
                            print(res.images)
                            res.images.map {
                                images.find { image ->
                                    image.orderRank == it.orderRank
                                            && image.description == it.description
                                            &src/main/kotlin/models/authorization/PasswordsModel.kt& image.url == it.url
                                } ?: throw Exception("failed to find returned image")
                            }
                        }
                    }
                }
            }
        }
        // endregion

        given("updateRecord") {
            then("getComposition") {}
        }

        given("batchUpdateRecords") {
            then("getComposition") {}
        }
    }
}