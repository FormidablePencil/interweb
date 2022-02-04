package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.texts.TextToCollection
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback
import shared.testUtils.texts

class TextRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    lateinit var textRepository: TextRepository

    init {
        beforeEach {
            clearAllMocks()
            textRepository = TextRepository()
        }

        // TODO("Exceptions implemented at low level, now need to be handled")
        given("insertRecord") {
            And("addRecordCollection") {
                And("createRecordToCollectionRelationship") {
                    then("getRecordOfCollection") {
                        rollback {
                            val resRecords = textRepository.insertRecord(texts[0])
                                ?: throw Exception("failed to insert image")
                            val collectionId = textRepository.addRecordCollection()
                            val hasCreatedRelations = textRepository.associateRecordToCollection(
                                TextToCollection(
                                    textId = resRecords.id!!,
                                    collectionId = collectionId,
                                    orderRank = resRecords.orderRank
                                )
                            )
                            hasCreatedRelations shouldBe true
                            textRepository.getRecordOfCollection(resRecords.id!!, collectionId)
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
                            val resRecords = textRepository.batchInsertRecords(texts)
                            resRecords.map { it.id shouldNotBe null }

                            val collectionId = textRepository.addRecordCollection()

                            val hasCreatedRelations =
                                textRepository.batchAssociateRecordsToCollection(resRecords, collectionId)
                            hasCreatedRelations shouldBe true

                            val (aResRecords, id) = textRepository.getCollectionOfRecords(collectionId)

                            aResRecords shouldNotBe null
                            print(aResRecords)
                            aResRecords.size shouldBe texts.size // todo - test fails because more than was given is returned
                            aResRecords.map {
                                texts.find { record ->
                                    record.orderRank == it.orderRank
                                            && record.text == it.text
                                } ?: throw Exception("failed to find returned image")
                            }
                        }
                    }
                }
            }
        }
//        given("updateTo") {
//            then("getComposition") {}
//        }
//
//        given("batchUpdateRecords") {
//            then("getComposition") {}
//        }
    }
}
