package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import shared.appEnvMockHelper
import shared.testUtils.carouselBasicImagesReq
import shared.testUtils.privilegedAuthors

class CarouselOfImagesManagerTest : BehaviorSpec({
    val textRepository: TextRepository = mockk()
    val imageRepository: ImageRepository = mockk()
    val compositionPrivilegesRepository: CompositionPrivilegesRepository = mockk()
    val compositionPrivilegesManager: CompositionPrivilegesManager = mockk()
    val carouselOfImagesRepository: CarouselOfImagesRepository = mockk()
    val appEnv: AppEnv = mockk()

    val userId = 1
    val idOfNewlyCreatedImageCollection = 12
    val idOfNewlyCreatedTextCollection = 43
    val privilegeSourceId = 89
    val carouselOfImagesComposePrepared = CarouselOfImagesComposePrepared(
        imageCollectionId = idOfNewlyCreatedImageCollection,
        redirectTextCollectionId = idOfNewlyCreatedTextCollection,
        privilegeId = privilegeSourceId,
        name = carouselBasicImagesReq.name,
    )
    val idOfNewlyCreatedCarouselOfImages = 12

    val carouselOfImagesManager = spyk(
        CarouselOfImagesManager(
            compositionPrivilegesManager = compositionPrivilegesManager,
            textRepository = textRepository,
            imageRepository = imageRepository,
            compositionPrivilegesRepository = compositionPrivilegesRepository,
            carouselOfImagesRepository = carouselOfImagesRepository
        )
    )

    beforeEach {
        clearAllMocks()
        appEnvMockHelper(appEnv, carouselOfImagesManager)
        println("test that each then statement is executes beforeEach beforehand")
    }

    // TODO("Exceptions implemented at low level, now need to be handled")

    given("createComposition") {
        beforeEach {
            // region setup
            every { imageRepository.batchInsertRecordsToNewCollection(carouselBasicImagesReq.images) } returns
                    Pair(carouselBasicImagesReq.images, idOfNewlyCreatedImageCollection)
            every { textRepository.batchInsertRecordsToNewCollection(carouselBasicImagesReq.imgOnclickRedirects) } returns
                    Pair(carouselBasicImagesReq.imgOnclickRedirects, idOfNewlyCreatedTextCollection)
            every { compositionPrivilegesRepository.addPrivilegeSource() } returns privilegeSourceId
            justRun {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            }
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) } returns idOfNewlyCreatedCarouselOfImages
            // endregion
        }

        then("transaction rollback on throw") {

        }


        then("") {
            // region setup
            every { imageRepository.batchInsertRecordsToNewCollection(carouselBasicImagesReq.images) } throws
                    CompositionExceptionReport(FailedToInsertRecord, this::class.java)
            // endregion

            val ex = shouldThrow<CompositionExceptionReport> {
                carouselOfImagesManager.createComposition(carouselBasicImagesReq, userId)
            }
            ex.clientMsg shouldBe CompositionCode.getClientMsg(FailedToInsertRecord)
        }

        then("UserNotPrivileged exception") {
            // region setup
            every {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            } throws CompositionException(UserNotPrivileged)
            // endregion

        }

        then("FailedToGetAuthorByUsername exception") {
            // region setup
            every {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            } throws CompositionException(FailedToFindAuthorByUsername)
            // endregion

            justRun { appEnv.database.useTransaction { it.rollback() } }

            val ex = shouldThrowExactly<CompositionException> {
                carouselOfImagesManager.createComposition(carouselBasicImagesReq, userId)
            }

            ex.code shouldBe UserNotPrivileged
        }

        then("fails compose edge case") {
            // region setup
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) } returns null
            // endregion

            val ex = shouldThrow<CompositionExceptionReport> {
                carouselOfImagesManager.createComposition(carouselBasicImagesReq, userId)
            }

            verify {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            }

            ex.clientMsg shouldBe CompositionCode.getClientMsg(FailedToCompose)
        }

        then("success") {
            // region setup
            // endregion

            val res = carouselOfImagesManager.createComposition(carouselBasicImagesReq, userId)

            res.isSuccess shouldBe true
            res.message() shouldBe null
            res.data shouldBe idOfNewlyCreatedCarouselOfImages
        }
    }
})
