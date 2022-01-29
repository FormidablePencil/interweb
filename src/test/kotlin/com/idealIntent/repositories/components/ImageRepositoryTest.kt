package com.idealIntent.repositories.components

import com.idealIntent.configurations.DIHelper
import dtos.libOfComps.genericStructures.images.Image
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
            then("getAssortmentById") {
                rollback {
                    val collectionOf = "Pet Projects"
                    val images = listOf(
                        Image(
                            orderRank = 10000,
                            imageTitle = "PAO Mnemonic System",
                            imageUrl = " https://i.ibb.co/1K7jQJw/pao.png"
                        ),
                        Image(
                            orderRank = 20000,
                            imageTitle = "Emoji Tack Toes",
                            imageUrl = "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
                        ),
                        Image(
                            orderRank = 30000,
                            imageTitle = "Crackalackin",
                            imageUrl = "https://i.ibb.co/4YP7yDb/crackalackin.png"
                        ),
                        Image(
                            orderRank = 40000,
                            imageTitle = "Pokedex",
                            imageUrl = "https://i.ibb.co/w0m4pF3/pokedex.png"
                        ),
                    )

                    val imageCollectionId = imageRepository.batchInsertNewRecords(images, collectionOf)
                        ?: throw Exception("failed to get id")
                    val res = imageRepository.getAssortmentById(imageCollectionId)

                    res shouldNotBe null
                    res.images.size shouldBe images.size
                    res.images.map {
                        images.find { image ->
                            image.orderRank == it.orderRank
                                    && image.imageTitle == it.imageTitle
                                    && image.imageUrl == it.imageUrl
                        } ?: throw Exception("failed to find returned image")
                    }
                }
            }
        }

        given("updateRecord") {
            then("getAssortmentById") {}
        }

        given("batchUpdateRecords") {
            then("getAssortmentById") {}
        }
    }
}