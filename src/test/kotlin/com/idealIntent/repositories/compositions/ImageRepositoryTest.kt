package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import dtos.collectionsGeneric.images.Image
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class ImageRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var imageRepository: ImageRepository

        beforeEach {
            imageRepository = ImageRepository()
        }

        given("batchInsertNewRecords") {
            then("getComposition") {
                rollback {
                    val label = "Pet Projects"
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

                    val imageCollectionId = imageRepository.batchInsertNewRecords(images)
                        ?: throw Exception("failed to get id")
                    val res = imageRepository.getCollectionOfRecords(0, 0) // todo

                    res shouldNotBe null
                    res.images.size shouldBe images.size
                    res.images.map {
                        images.find { image ->
                            image.orderRank == it.orderRank
                                    && image.description == it.description
                                    && image.url == it.url
                        } ?: throw Exception("failed to find returned image")
                    }
                }
            }
        }

        given("updateRecord") {
            then("getComposition") {}
        }

        given("batchUpdateRecords") {
            then("getComposition") {}
        }
    }
}