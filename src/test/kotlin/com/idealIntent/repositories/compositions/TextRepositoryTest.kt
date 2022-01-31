package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import io.kotest.koin.KoinListener
import shared.testUtils.BehaviorSpecUtRepo

class TextRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
//        lateinit var textRepository: TextRepository
//
//        beforeEach {
//            textRepository = TextRepository()
//        }
//
//        given("batchInsertRecords") {
//            then("getComposition") {
//                rollback {
//                    val label = "random texts"
//                    val texts = listOf(
//                        Text(orderRank = 10000, text = "space"),
//                        Text(orderRank = 20000, text = "people"),
//                        Text(orderRank = 30000, text = "man"),
//                    )
//
//                    val id = textRepository.batchInsertNewRecords(texts)
//                        ?: throw Exception("failed to insert records")
//                    val res = textRepository.getCollectionOfRecords(id)
//
//                    res.label shouldBe label
//                    res.texts.size shouldBe texts.size
//                    res.texts.map {
//                        val text = texts.find { text -> text.orderRank == it.orderRank }
//                            ?: throw Exception("failed to find returned image")
//                        text.text shouldBe it.text
//                    }
//                }
//            }
//        }
//
//        given("updateTo") {
//            then("getComposition") {}
//        }
//
//        given("batchUpdateRecords") {
//            then("getComposition") {}
//        }
    }
}
