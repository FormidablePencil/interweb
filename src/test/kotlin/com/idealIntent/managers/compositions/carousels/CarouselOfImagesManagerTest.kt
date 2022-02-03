package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import io.kotest.assertions.throwables.shouldThrow
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
    val carouselOfImagesRepository: CarouselOfImagesRepository = mockk()
    val appEnv: AppEnv = mockk()

    val idOfNewlyCreatedImageCollection = 12
    val idOfNewlyCreatedTextCollection = 43
    val privilegeId = 89
    val carouselOfImagesComposePrepared = CarouselOfImagesComposePrepared(
        imageCollectionId = idOfNewlyCreatedImageCollection,
        redirectTextCollectionId = idOfNewlyCreatedTextCollection,
        privilegeId = privilegeId,
        name = carouselBasicImagesReq.name,
    )
    val idOfNewlyCreatedCarouselOfImages = 12

    val carouselOfImagesManager = spyk(
        CarouselOfImagesManager(
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

    given("createComposition") {
        beforeEach {
            // region setup
            every { imageRepository.batchInsertRecordsToNewCollection(carouselBasicImagesReq.images) } returns
                    Pair(carouselBasicImagesReq.images, idOfNewlyCreatedImageCollection)
            every { textRepository.batchInsertRecordsToNewCollection(carouselBasicImagesReq.imgOnclickRedirects) } returns
                    Pair(carouselBasicImagesReq.imgOnclickRedirects, idOfNewlyCreatedTextCollection)
            every { compositionPrivilegesRepository.addPrivilegeSource() } returns privilegeId
            every {
                compositionPrivilegesRepository.giveMultipleAuthorsPrivilegesByUsername(privilegedAuthors, privilegeId)
            } returns Pair(true, null)
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) } returns idOfNewlyCreatedCarouselOfImages
            // endregion setup
        }
        then("reverts transaction if exception is thrown") {
            // region setup
            every { imageRepository.batchInsertRecordsToNewCollection(carouselBasicImagesReq.images) } throws
                    CompositionExceptionReport(CompositionCode.FailedToInsertRecord, this::class.java)
            // endregion setup

            val ex = shouldThrow<CompositionExceptionReport> {
                carouselOfImagesManager.createComposition(carouselBasicImagesReq)
            }
            ex.clientMsg shouldBe CompositionCode.getClientMsg(CompositionCode.FailedToInsertRecord)
        }
        then("reverts edge case for when composing fails") {
            // region setup
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) } returns null
            // endregion setup

            val ex = shouldThrow<CompositionExceptionReport> {
                carouselOfImagesManager.createComposition(carouselBasicImagesReq)
            }
            ex.clientMsg shouldBe CompositionCode.getClientMsg(CompositionCode.FailedToCompose)
        }
        then("transaction rolls back when failed at author look up") {
            // region setup
            every {
                compositionPrivilegesRepository.giveMultipleAuthorsPrivilegesByUsername(privilegedAuthors, privilegeId)
            } returns Pair(false, privilegedAuthors[0].username)
            // endregion setup

            justRun { appEnv.database.useTransaction { it.rollback() } }

            val res = carouselOfImagesManager.createComposition(carouselBasicImagesReq)
            res.isSuccess shouldBe false
            res.message() shouldBe CompositionCode.getClientMsg(CompositionCode.FailedAtAuthorLookup)
            res.failedResponseData shouldBe privilegedAuthors[0].username
        }
        then("success") {
            // region setup
            every {
                compositionPrivilegesRepository.giveMultipleAuthorsPrivilegesByUsername(privilegedAuthors, privilegeId)
            } returns Pair(true, null)
            // endregion setup

            val res = carouselOfImagesManager.createComposition(carouselBasicImagesReq)
            res.isSuccess shouldBe true
            res.message() shouldBe null
            res.data shouldBe idOfNewlyCreatedCarouselOfImages
        }
    }
})
