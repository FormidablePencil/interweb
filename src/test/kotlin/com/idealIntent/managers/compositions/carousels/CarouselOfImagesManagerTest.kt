package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionCode.FailedToFindAuthorByUsername
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.*
import shared.appEnvMockHelper
import shared.testUtils.createCarouselBasicImagesReq
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
        sourceId = privilegeSourceId,
        name = createCarouselBasicImagesReq.name,
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
    }

    given("createComposition") {
        beforeEach {
            // region setup
            every { imageRepository.batchInsertRecordsToNewCollection(createCarouselBasicImagesReq.images) } returns
                    carouselOfImagesComposePrepared.imageCollectionId
            every { textRepository.batchInsertRecordsToNewCollection(createCarouselBasicImagesReq.imgOnclickRedirects) } returns
                    carouselOfImagesComposePrepared.redirectTextCollectionId
            every { compositionPrivilegesManager.createPrivileges(userId) } returns
                    carouselOfImagesComposePrepared.sourceId
            justRun {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            }
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) } returns idOfNewlyCreatedCarouselOfImages
            // endregion
        }

        then("provided a username that could not find author by") {
            // region setup
            every {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            } throws CompositionException(FailedToFindAuthorByUsername)
            // endregion

            val res = carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, userId)

            verify { appEnv.database.useTransaction { } }
            res.code shouldBe FailedToFindAuthorByUsername
            res.message() shouldBe CompositionCode.getClientMsg(FailedToFindAuthorByUsername)
            res.statusCode() shouldBe HttpStatusCode.BadRequest
        }

        then("success") {
            val res = carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, userId)

            verify { imageRepository.batchInsertRecordsToNewCollection(createCarouselBasicImagesReq.images) }
            verify { textRepository.batchInsertRecordsToNewCollection(createCarouselBasicImagesReq.imgOnclickRedirects) }
            verify { compositionPrivilegesManager.createPrivileges(userId) }
            verify {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeSourceId, userId
                )
            }
            verify { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) }

            res.isSuccess shouldBe true
            res.message() shouldBe null
            res.data shouldBe idOfNewlyCreatedCarouselOfImages
        }
    }
})
