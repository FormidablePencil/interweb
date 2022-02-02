package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper

import io.kotest.koin.KoinListener
import shared.testUtils.BehaviorSpecUtRepo

// NOTE: could be just a BehaviorSpec
class CarouselRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
//        lateinit var carouselRepository: CarouselOfImagesRepository
//
//        val carouselBasicImages = CarouselBasicImages(
//            name = "Pet Projects",
//            images = listOf(
//                Image(
//                    id = null,
//                    orderRank = 10000,
//                    description = "PAO Mnemonic System",
//                    url = " https://i.ibb.co/1K7jQJw/pao.png"
//                ),
//                Image(
//                    id = null,
//                    orderRank = 20000,
//                    description = "Crackalackin",
//                    url = "https://i.ibb.co/4YP7yDb/crackalackin.png"
//                ),
//                Image(
//                    id = null,
//                    orderRank = 30000,
//                    description = "Emoji Tack Toes",
//                    url = "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
//                ),
//                Image(
//                    id = null,
//                    orderRank = 40000,
//                    description = "Pokedex",
//                    url = "https://i.ibb.co/w0m4pF3/pokedex.png"
//                )
//            ),
//            navToCorrespondingImagesOrder = listOf(
//                Text(orderRank = 10000, text = "/paosystem"),
//                Text(orderRank = 20000, text = "/emojitacktoes"),
//                Text(orderRank = 30000, text = "/crackalackin"),
//                Text(orderRank = 40000, text = "/pokedex"),
//            ),
//            privilegedAuthors = listOf(
//                PrivilegedAuthorsToComposition(modLvl = 1, authorId = 84),
//                PrivilegedAuthorsToComposition(modLvl = 1, authorId = 2)
//            )
//        )
//
//        beforeEach {
//            carouselRepository = CarouselOfImagesRepository(TextRepository(), ImageRepository(), PrivilegeRepository())
//        }
//
//        given("createComposition && getComposition") {
//            then("should have been created") {
//                rollback {
//                    val savedDataId = carouselRepository.createComposition(carouselBasicImages)
//                        ?: throw Exception("failed to save data")
//
//                    val carouselOfImages = carouselRepository.getComposition(savedDataId)
//
//                    carouselOfImages.name shouldBe carouselBasicImages.name
//
//                    // region asserting images
//                    carouselOfImages.images.size shouldBe carouselBasicImages.images.size
//                    carouselOfImages.images.map {
//                        val item = carouselBasicImages.images.find { item ->
//                            item.orderRank == it.orderRank
//                        } ?: throw Exception("no item in orderRank")
//                        it.description shouldBe item.description
//                        it.url shouldBe item.url
//                    }
//                    // endregion
//
//                    // region asserting navToCorrespondingImagesOrder
//                    carouselOfImages.navToCorrespondingImagesOrder.size shouldBe carouselBasicImages.navToCorrespondingImagesOrder.size
//                    carouselOfImages.navToCorrespondingImagesOrder.map {
//                        val item = carouselBasicImages.navToCorrespondingImagesOrder.find { item ->
//                            item.orderRank == it.orderRank
//                        } ?: throw Exception("no item in orderRank")
//                        item.text shouldBe it.text
//                    }
//                    // endregion
//
//                    // region asserting privilegedAuthors
//                    carouselOfImages.privilegedAuthors.size shouldBe carouselBasicImages.privilegedAuthors.size
////                    carouselOfImages.privilegedAuthors.map {
////                        val item = carouselBasicImages.privilegedAuthors.find { item ->
////                            item.modLvl == it.modLvl
////                                    && item.authorId == it.authorId
////                        } ?: throw Exception("privilegedAuthors invalid")
////                    }
//                    // endregion
//                }
//            }
//        }
    }
}