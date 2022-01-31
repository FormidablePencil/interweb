package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.collectionsGeneric.images.Image
import dtos.collectionsGeneric.privileges.AuthorToPrivilege
import dtos.collectionsGeneric.texts.Text
import dtos.compositions.carousels.CarouselBasicImages
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

// NOTE: could be just a BehaviorSpec
class CarouselRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
//        lateinit var carouselRepository: CarouselOfImagesRepository
//
//        val carouselBasicImages = CarouselBasicImages(
//            title = "Pet Projects",
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
//                AuthorToPrivilege(modLvl = 1, authorId = 84),
//                AuthorToPrivilege(modLvl = 1, authorId = 2)
//            )
//        )
//
//        beforeEach {
//            carouselRepository = CarouselOfImagesRepository(TextRepository(), ImageRepository(), PrivilegeRepository())
//        }
//
//        given("insertComposition && getComposition") {
//            then("should have been created") {
//                rollback {
//                    val savedDataId = carouselRepository.insertComposition(carouselBasicImages)
//                        ?: throw Exception("failed to save data")
//
//                    val carouselOfImages = carouselRepository.getComposition(savedDataId)
//
//                    carouselOfImages.title shouldBe carouselBasicImages.title
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