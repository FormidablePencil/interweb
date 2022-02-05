package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.test.inject
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.giveIdsToImages
import shared.testUtils.images
import shared.testUtils.rollback

// todo
class ImageRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val imageRepository: ImageRepository by inject()

    init {
        beforeEach {
            clearAllMocks()
        }

        // region Get
        given("getSingleRecordOfCollection") {

            then("success") {
                rollback {
                    val collectionId = imageRepository.insertRecordToNewCollection(images[0])
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to find collection by id")
                    val record = records.find { it.orderRank == images[0].orderRank }
                        ?: throw failure("failed to retrieve saved record")
                    val res = imageRepository.getSingleRecordOfCollection(record.id, collectionId)
                        ?: throw failure("failed to retrieve single record")
                    res.url shouldBe record.url
                }
            }
        }

        given("getAllRecordsOfCollection") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.batchInsertRecordsToNewCollection(images)
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to retrieve records")
                    records.size shouldBe images.size
                    records.forEach { record ->
                        val found = records.find { requestersText -> requestersText.orderRank == record.orderRank }
                            ?: throw failure("getAllRecordsOfCollection failed to return images saved")
                        found.url shouldBe record.url
                    }
                }
            }
        }

        given("getRecordsQuery") {
            // Not meant to be tested. This method would have been private
            // if it wasn't in ICollectionStructure protocol.
        }

        given("validateRecordToCollectionRelationship") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.insertRecordToNewCollection(images[0])
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("to get records after insertRecordToNewCollection")

                    imageRepository.validateRecordToCollectionRelationship(records[0].id, collectionId)
                }
            }
        }
        // endregion Get


        // region Insert
        given("batchInsertRecordsToNewCollection") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.batchInsertRecordsToNewCollection(images)
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("couldn't retrieve records by collection id")
                    records.map {
                        images.find { record ->
                            record.orderRank == it.orderRank
                                    && record.url == it.url
                        } ?: throw failure("failed to find returned record")
                    }
                }
            }
        }

        given("batchInsertRecordsToCollection") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.addRecordCollection()
                    if (!imageRepository.batchInsertRecordsToCollection(images, collectionId))
                        throw failure("failed to insert new records under an existing collection")
                }
            }
        }

        given("insertRecordToCollection") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.addRecordCollection()
                    imageRepository.insertRecordToCollection(images[0], collectionId)
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("couldn't retrieve records by collection id")
                    records[0].url shouldBe images[0].url
                }
            }
        }

        given("addRecordCollection") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.addRecordCollection()
                    collectionId shouldNotBe null
                }
            }
        }

        given("batchAssociateRecordsToCollection") {
            then("provided an empty list") {
                rollback {
                    val emptyList = listOf<ImagePK>()
                    val collectionId = imageRepository.addRecordCollection()
                    val ex = shouldThrowExactly<CompositionException> {
                        imageRepository.batchAssociateRecordsToCollection(emptyList, collectionId)
                    }
                    ex.code shouldBe CompositionCode.EmptyListOfRecordsProvided
                }
            }

            then("non-existent record id, existent collection id") {
                rollback {
                    val collectionId = imageRepository.batchInsertRecordsToNewCollection(images)
                    val ex = shouldThrowExactly<CompositionException> {
                        imageRepository.batchAssociateRecordsToCollection(giveIdsToImages(), collectionId)
                    }
                    ex.code shouldBe CompositionCode.FailedToAssociateRecordToCollection
                }
            }

            then("existent record id, non-existent collection id") {
                rollback {
                    val collectionId = imageRepository.batchInsertRecordsToNewCollection(images)
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("didn't get collection of all records saved")
                    val ex = shouldThrowExactly<CompositionException> {
                        imageRepository.batchAssociateRecordsToCollection(records, 888888888)
                    }
                    ex.code shouldBe CompositionCode.FailedToAssociateRecordToCollection
                }
            }
        }

        given("associateRecordToCollection") {
            then("success") {
                rollback {
                    val collectionId = imageRepository.batchInsertRecordsToNewCollection(images)
                    val records = imageRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to retrieve saved records")

                    val collection2Id = imageRepository.batchInsertRecordsToNewCollection(images)
                    imageRepository.associateRecordToCollection(
                        orderRank = records[0].orderRank, recordId = records[0].id, collectionId = collectionId
                    )
                }
            }
        }
        // endregion Insert


        // region Update
        // endregion Update

        // region Delete
        // endregion Delete
    }
}
