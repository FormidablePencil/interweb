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
                null,
                10000,
                "PAO Mnemonic System",
                " https://i.ibb.co/1K7jQJw/pao.png"
            ),
            Image(
                null,
                20000,
                "Emoji Tack Toes",
                "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
            ),
            Image(
                null,
                30000,
                "Crackalackin",
                "https://i.ibb.co/4YP7yDb/crackalackin.png"
            ),
            Image(
                null,
                40000,
                "Pokedex",
                "https://i.ibb.co/w0m4pF3/pokedex.png"
            ),
        )

        // region Insert
        given("insertRecord") {
            And("addRecordCollection") {
                And("createRecordToCollectionRelationship") {
                    then("getRecordOfCollection") {
                        rollback {
                            val resRecords = imageRepository.insertRecord(images[0])
                                ?: throw Exception("failed to insert image")
                            val collectionId = imageRepository.addRecordCollection()
                            val hasCreatedRelations = imageRepository.createRecordToCollectionRelationship(
                                ImageToCollection(
                                    imageId = resRecords.id!!,
                                    collectionId = collectionId,
                                    orderRank = resRecords.orderRank
                                )
                            )
                            hasCreatedRelations shouldBe true
                            imageRepository.getRecordOfCollection(resRecords.id!!, collectionId)
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
                            val resRecords = imageRepository.batchInsertRecords(images)
                            resRecords.map { it.id shouldNotBe null }

                            val collectionId = imageRepository.addRecordCollection()

                            val hasCreatedRelations =
                                imageRepository.batchCreateRecordToCollectionRelationship(resRecords, collectionId)
                            hasCreatedRelations shouldBe true

                            val (aResRecords, id) = imageRepository.getCollectionOfRecords(collectionId)

                            aResRecords shouldNotBe null
                            print(aResRecords)
                            aResRecords.size shouldBe images.size // todo - test fails because more than was given is returned
                            aResRecords.map {
                                images.find { record ->
                                    record.orderRank == it.orderRank
                                            && record.description == it.description
                                            && record.url == it.url
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
