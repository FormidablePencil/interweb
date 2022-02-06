package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.exceptions.CompositionCode.FailedToAssociateRecordToCollection
import com.idealIntent.exceptions.CompositionCode.EmptyListOfRecordsProvided
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.giveIdsToTexts
import shared.testUtils.rollback
import shared.testUtils.texts

class TextRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val textRepository: TextRepository by inject()

    init {
        beforeEach { clearAllMocks() }

        // region Get
        given("getSingleRecordOfCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.insertRecordToNewCollection(texts[0])
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to find collection by id")
                    val record = records.find { it.orderRank == texts[0].orderRank }
                        ?: throw failure("failed to retrieve saved record")
                    val res = textRepository.getSingleRecordOfCollection(record.id, collectionId)
                        ?: throw failure("failed to retrieve single record")
                    res.text shouldBe record.text
                }
            }
        }

        given("getAllRecordsOfCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.batchInsertRecordsToNewCollection(texts)
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to retrieve records")
                    records.size shouldBe texts.size
                    records.forEach { record ->
                        val found = texts.find { requestersText -> requestersText.orderRank == record.orderRank }
                            ?: throw failure("getAllRecordsOfCollection failed to return images saved")
                        found.text shouldBe record.text
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
                    val collectionId = textRepository.insertRecordToNewCollection(texts[0])
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("to get records after insertRecordToNewCollection")

                    textRepository.validateRecordToCollectionRelationship(records[0].id, collectionId)
                }
            }
        }
        // endregion Get


        // region Insert
        given("batchInsertRecordsToNewCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.batchInsertRecordsToNewCollection(texts)
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("couldn't retrieve records by collection id")
                    records.map {
                        texts.find { record ->
                            record.orderRank == it.orderRank
                                    && record.text == it.text
                        } ?: throw failure("failed to find returned record")
                    }
                }
            }
        }

        given("batchInsertRecordsToCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.addRecordCollection()
                    if (!textRepository.batchInsertRecordsToCollection(texts, collectionId))
                        throw failure("failed to insert new records under an existing collection")
                }
            }
        }

        given("insertRecordToCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.addRecordCollection()
                    textRepository.insertRecordToCollection(texts[0], collectionId)
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("couldn't retrieve records by collection id")
                    records[0].text shouldBe texts[0].text
                }
            }
        }

        given("addRecordCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.addRecordCollection()
                    collectionId shouldNotBe null
                }
            }
        }

        given("batchAssociateRecordsToCollection") {
            then("provided an empty list") {
                rollback {
                    val emptyList = listOf<TextPK>()
                    val collectionId = textRepository.addRecordCollection()
                    val ex = shouldThrowExactly<CompositionException> {
                        textRepository.batchAssociateRecordsToCollection(emptyList, collectionId)
                    }
                    ex.code shouldBe EmptyListOfRecordsProvided
                }
            }

            then("non-existent record id, existent collection id") {
                rollback {
                    val collectionId = textRepository.batchInsertRecordsToNewCollection(texts)
                    val ex = shouldThrowExactly<CompositionException> {
                        textRepository.batchAssociateRecordsToCollection(giveIdsToTexts(), collectionId)
                    }
                    ex.code shouldBe FailedToAssociateRecordToCollection
                }
            }

            then("existent record id, non-existent collection id") {
                rollback {
                    val collectionId = textRepository.batchInsertRecordsToNewCollection(texts)
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("didn't get collection of all records saved")
                    val ex = shouldThrowExactly<CompositionException> {
                        textRepository.batchAssociateRecordsToCollection(records, 888888888)
                    }
                    ex.code shouldBe FailedToAssociateRecordToCollection
                }
            }
        }

        given("associateRecordToCollection") {
            then("success") {
                rollback {
                    val collectionId = textRepository.batchInsertRecordsToNewCollection(texts)
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to retrieve saved records")

                    val collection2Id = textRepository.batchInsertRecordsToNewCollection(texts)
                    textRepository.associateRecordToCollection(
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