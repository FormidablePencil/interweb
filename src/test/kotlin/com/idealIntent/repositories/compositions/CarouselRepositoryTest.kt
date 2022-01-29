package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.genericStructures.images.Image
import dtos.compositions.genericStructures.privileges.PrivilegedAuthor
import dtos.compositions.genericStructures.texts.Text
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

// NOTE: could be just a BehaviorSpec
class CarouselRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var carouselRepository: CarouselOfImagesRepository

        val carouselBasicImages = CarouselBasicImages(
            title = "Pet Projects",
            images = listOf(
                Image(
                    orderRank = 10000,
                    imageTitle = "PAO Mnemonic System",
                    imageUrl = " https://i.ibb.co/1K7jQJw/pao.png"
                ),
                Image(
                    orderRank = 20000,
                    imageTitle = "Crackalackin",
                    imageUrl = "https://i.ibb.co/4YP7yDb/crackalackin.png"
                ),
                Image(
                    orderRank = 30000,
                    imageTitle = "Emoji Tack Toes",
                    imageUrl = "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
                ),
                Image(
                    orderRank = 40000,
                    imageTitle = "Pokedex",
                    imageUrl = "https://i.ibb.co/w0m4pF3/pokedex.png"
                )
            ),
            navToCorrespondingImagesOrder = listOf(
                Text(orderRank = 10000, text = "/paosystem"),
                Text(orderRank = 20000, text = "/emojitacktoes"),
                Text(orderRank = 30000, text = "/crackalackin"),
                Text(orderRank = 40000, text = "/pokedex"),
            ),
            privilegedAuthors = listOf(
                PrivilegedAuthor(modLvl = 1, authorId = 84),
                PrivilegedAuthor(modLvl = 1, authorId = 2)
            )
        )

        beforeEach {
            carouselRepository = CarouselOfImagesRepository(TextRepository(), ImageRepository(), PrivilegeRepository())
        }

        given("insertNewComposition && getAssortmentById") {
            then("should have been created") {
                rollback {
                    val savedDataId = carouselRepository.insertNewComposition(carouselBasicImages)
                        ?: throw Exception("failed to save data")

                    val carouselOfImages = carouselRepository.getAssortmentById(savedDataId)

                    carouselOfImages.title shouldBe carouselBasicImages.title

                    // region asserting images
                    carouselOfImages.images.size shouldBe carouselBasicImages.images.size
                    carouselOfImages.images.map {
                        val item = carouselBasicImages.images.find { item ->
                            item.orderRank == it.orderRank
                        } ?: throw Exception("no item in orderRank")
                        it.imageTitle shouldBe item.imageTitle
                        it.imageUrl shouldBe item.imageUrl
                    }
                    // endregion

                    // region asserting navToCorrespondingImagesOrder
                    carouselOfImages.navToCorrespondingImagesOrder.size shouldBe carouselBasicImages.navToCorrespondingImagesOrder.size
                    carouselOfImages.navToCorrespondingImagesOrder.map {
                        val item = carouselBasicImages.navToCorrespondingImagesOrder.find { item ->
                            item.orderRank == it.orderRank
                        } ?: throw Exception("no item in orderRank")
                        item.text shouldBe it.text
                    }
                    // endregion

                    // region asserting privilegedAuthors
                    carouselOfImages.privilegedAuthors.size shouldBe carouselBasicImages.privilegedAuthors.size
                    carouselOfImages.privilegedAuthors.map {
                        val item = carouselBasicImages.privilegedAuthors.find { item ->
                            item.modLvl == it.modLvl
                                    && item.authorId == it.authorId
                        } ?: throw Exception("privilegedAuthors invalid")
                    }
                    // endregion
                }
            }
        }
    }
}