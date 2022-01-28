package repositories.components

import configurations.DIHelper
import dtos.libOfComps.genericStructures.texts.Text
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class TextRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var textRepository: TextRepository

        beforeEach {
            textRepository = TextRepository()
        }

        given("insertCollectionOfTexts") {
            then("getAssortmentById") {
                rollback {
                    val collectionOf = "random texts"
                    val texts = listOf(
                        Text(orderRank = 10000, text = "space"),
                        Text(orderRank = 20000, text = "people"),
                        Text(orderRank = 30000, text = "man"),
                    )

                    val id = textRepository.insertCollectionOfTexts(texts, collectionOf)
                        ?: throw Exception("failed to get id")
                    val res = textRepository.getAssortmentById(id)

                    res.collectionOf shouldBe collectionOf
                    res.texts.size shouldBe texts.size
                    res.texts.map {
                        val text = texts.find { text -> text.orderRank == it.orderRank }
                            ?: throw Exception("failed to find returned image")
                        text.text shouldBe it.text
                    }
                }
            }
        }

        given("updateText") {
            then("getAssortmentById") {}
        }

        given("batchUpdateTexts") {
            then("getAssortmentById") {}
        }
    }
}
