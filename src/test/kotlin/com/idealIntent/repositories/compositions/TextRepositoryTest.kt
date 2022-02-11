package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositionCRUD.UpdateColumn
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import dtos.collectionsGeneric.texts.TextsCOL
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

        given("insertRecordToNewCollection") {
            then("success") {
                rollback {
                    textRepository.insertRecordToNewCollection(texts[0]) shouldNotBe null
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


        given("updateRecord") {

            then("provided not a number for updating order rank") {
                rollback {
                    // region Setup
                    val updateOrderRankTo = "abcdefg"
                    val collectionId = textRepository.insertRecordToNewCollection(texts[0])
                    val records = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to get record saved earlier")

                    val recordUpdate = RecordUpdate(
                        recordId = records[0].id,
                        updateTo = listOf(UpdateColumn(TextsCOL.OrderRank.value, updateOrderRankTo))
                    )
                    // endregion

                    val ex = shouldThrowExactly<CompositionException> {
                        textRepository.updateRecord(recordUpdate)
                    }.code shouldBe ProvidedStringInPlaceOfInt
                }
            }

            then("successfully update record") {
                val updateToValueOf = "updated value"
                rollback {
                    // region Setup
                    val collectionId = textRepository.insertRecordToNewCollection(texts[0])
                    textRepository.insertRecordToCollection(texts[1], collectionId)

                    val recordsBeforeUpdate = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to get record saved earlier")

                    val recordBeforeUpdate = recordsBeforeUpdate.find { it.orderRank == texts[0].orderRank }
                        ?: throw failure("failed to get record saved earlier")

                    recordBeforeUpdate.text shouldBe texts[0].text

                    val recordUpdate = RecordUpdate(
                        recordId = recordBeforeUpdate.id,
                        updateTo = listOf(UpdateColumn(TextsCOL.Text.value, updateToValueOf))
                    )
                    // endregion Setup

                    textRepository.updateRecord(recordUpdate)

                    val recordsAfterUpdate = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to get record saved earlier")

                    // region Validate the change
                    val recordAfterUpdate = recordsAfterUpdate.find { it.orderRank == texts[0].orderRank }
                        ?: throw failure("failed to get record saved earlier")

                    recordAfterUpdate.text shouldNotBe texts[0].text
                    recordAfterUpdate.text shouldBe updateToValueOf
                    // endregion


                    // region Test that second record was not updated too
                    val record2 = recordsAfterUpdate.find { it.orderRank == texts[1].orderRank }
                        ?: throw failure("failed to get record saved earlier")

                    record2.text shouldBe texts[1].text
                    // endregion
                }
            }

            then("successfully update order rank of record") {
                val updateOrderRankTo = "80000"
                rollback {
                    // region Setup
                    val collectionId = textRepository.insertRecordToNewCollection(texts[0])
                    textRepository.insertRecordToCollection(texts[1], collectionId)

                    val recordsBeforeUpdate = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to get record saved earlier")

                    val recordBeforeUpdate = recordsBeforeUpdate.find { it.orderRank == texts[0].orderRank }
                        ?: throw failure("failed to get record saved earlier")

                    recordBeforeUpdate.text shouldBe texts[0].text

                    val recordUpdate = RecordUpdate(
                        recordId = recordBeforeUpdate.id,
                        updateTo = listOf(UpdateColumn(TextsCOL.OrderRank.value, updateOrderRankTo))
                    )
                    // endregion Setup

                    textRepository.updateRecord(recordUpdate)

                    val recordsAfterUpdate = textRepository.getAllRecordsOfCollection(collectionId)
                        ?: throw failure("failed to get record saved earlier")

                    // region Validate the change
                    recordsAfterUpdate.find {
                        it.orderRank == updateOrderRankTo.toInt()
                                && it.text == texts[0].text
                    } shouldNotBe null
                    // endregion


                    // region Test that the order rank of the second record was not updated too
                    val record2 = recordsAfterUpdate.find {
                        it.orderRank == texts[1].orderRank
                                && it.text == texts[1].text
                    } shouldNotBe null
                    // endregion
                }
            }
        }

        given("deleteRecordsCollection") {

            then("failed to delete collection of id because provided id does not exist thus nothing to delete") {
                rollback {
                    shouldThrowExactly<CompositionException> {
                        textRepository.deleteRecordsCollection(99999999)
                    }.code shouldBe CollectionOfRecordsNotFound
                }
            }

            then("successfully deleted collection of records and all related things") {
                rollback {
                    val collectionId = textRepository.insertRecordToNewCollection(texts[0])

                    textRepository.getAllRecordsOfCollection(collectionId) shouldNotBe null

                    textRepository.deleteRecordsCollection(collectionId)

                    textRepository.getAllRecordsOfCollection(collectionId) shouldBe null
                }
            }
        }
    }
}