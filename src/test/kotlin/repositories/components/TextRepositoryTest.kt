package repositories.components

import configurations.DIHelper
import dtos.libOfComps.genericStructures.Text
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
            then("should have inserted a collection of random texts") {
                rollback {
                    val images = listOf(
                        Text(orderRank = 10000, text = "space"),
                        Text(orderRank = 20000, text = "people"),
                        Text(orderRank = 30000, text = "man"),
                    )
                    val collectionOf = "random texts"

                    val id = textRepository.insertCollectionOfTexts(images, collectionOf)
                        ?: throw Exception("failed to get id")
                    val res = textRepository.getCollectionOfTextsById(id)

                    res.collectionOf shouldBe collectionOf
                    res.images.map {
                        images.find { image -> image.orderRank == it.orderRank }
                            ?: throw Exception("failed to find returned image")
                    }
                }
            }
        }
    }
}
