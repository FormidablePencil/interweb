package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.images.ImageToCollection
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import io.kotest.assertions.failure
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.test.inject
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.images
import shared.testUtils.rollback

class ImageRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val imageRepository: ImageRepository by inject()

    init {
        beforeEach {
            clearAllMocks()
        }

        // region Get
        given("getRecordOfCollection") {}

        given("getCollectionOfRecords") {}

        given("getRecordsQuery") {
            // Not meant to be tested. This method would have been private
            // if it wasn't in ICollectionStructure protocol.
        }

        given("getRecordToCollectionRelationship") {}
        // endregion Get


        // region Insert
        given("batchInsertRecordsToNewCollection") {}

        given("insertRecord") {
            And("addRecordCollection") {
                And("createRecordToCollectionRelationship") {
                    then("getRecordOfCollection") {
                        rollback {
                            val resRecords = imageRepository.insertRecord(images[0])
                                ?: throw Exception("failed to insert record")
                            val collectionId = imageRepository.addRecordCollection()
                            val hasCreatedRelations = imageRepository.associateRecordToCollection(
                                ImageToCollection(
                                    imageId = resRecords.id,
                                    collectionId = collectionId,
                                    orderRank = resRecords.orderRank
                                )
                            )
                            hasCreatedRelations shouldBe true
                            imageRepository.getRecordOfCollection(resRecords.id, collectionId)
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
                                imageRepository.batchAssociateRecordsToCollection(resRecords, collectionId)
                            hasCreatedRelations shouldBe true

                            val aResRecords = imageRepository.getCollectionOfRecords(collectionId)
                                ?: throw failure("should have returned records")

                            aResRecords.size shouldBe images.size // todo - test fails because more than was given is returned
                            aResRecords.map {
                                images.find { record ->
                                    record.orderRank == it.orderRank
                                            && record.description == it.description
                                            && record.url == it.url
                                } ?: throw Exception("failed to find returned record")
                            }
                        }
                    }
                }
            }
        }

        given("addRecordCollection") {}

        given("batchAssociateRecordsToCollection") {}

        given("associateRecordToCollection") {}
        // endregion Insert


        // region Update
        // endregion Update

        // region Delete
        // endregion Delete
    }
}
