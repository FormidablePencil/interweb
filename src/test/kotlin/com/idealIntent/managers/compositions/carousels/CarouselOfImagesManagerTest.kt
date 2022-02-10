package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode.FailedToFindAuthorByUsername
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import shared.appEnvMockHelper
import shared.testUtils.createPublicCarouselBasicImagesReq
import shared.testUtils.privilegedAuthors

class CarouselOfImagesManagerTest : BehaviorSpec({
    val textRepository: TextRepository = mockk()
    val imageRepository: ImageRepository = mockk()
    val compositionSourceRepository: CompositionSourceRepository = mockk()
    val compositionPrivilegesManager: CompositionPrivilegesManager = mockk()
    val carouselOfImagesRepository: CarouselOfImagesRepository = mockk()
    val spaceRepository: SpaceRepository = mockk()
    val appEnv: AppEnv = mockk()

    val authorId = 1
    val idOfNewlyCreatedImageCollection = 12
    val idOfNewlyCreatedTextCollection = 43
    val compositionSourceId = 89
    val layoutId = 54
    val carouselOfImagesComposePrepared = CarouselOfImagesComposePrepared(
        imageCollectionId = idOfNewlyCreatedImageCollection,
        redirectTextCollectionId = idOfNewlyCreatedTextCollection,
        sourceId = compositionSourceId,
        name = createPublicCarouselBasicImagesReq.name,
    )

    val carouselOfImagesManager = spyk(
        CarouselOfImagesManager(
            compositionPrivilegesManager = compositionPrivilegesManager,
            textRepository = textRepository,
            imageRepository = imageRepository,
            compositionSourceRepository = compositionSourceRepository,
            carouselOfImagesRepository = carouselOfImagesRepository,
            spaceRepository = spaceRepository,
        )
    )

    beforeEach {
        clearAllMocks()
        appEnvMockHelper(appEnv, carouselOfImagesManager)
    }

    given("createComposition") {
        beforeEach {
            // region setup
            every { imageRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesReq.images) } returns
                    carouselOfImagesComposePrepared.imageCollectionId
            every { textRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesReq.imgOnclickRedirects) } returns
                    carouselOfImagesComposePrepared.redirectTextCollectionId
            every {
                compositionPrivilegesManager.createCompositionSource(
                    compositionType = 0,
                    privilegeLevel = 0,
                    name = "some name",
                    authorId = authorId
                )
            } returns
                    carouselOfImagesComposePrepared.sourceId
            justRun {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, compositionSourceId, authorId
                )
            }
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) } returns compositionSourceId
            every {
                spaceRepository.associateCompositionToLayout(
                    orderRank = 0,
                    compositionSourceId = compositionSourceId,
                    layoutId = layoutId
                )
            } returns true
            // endregion
        }

        then("provided a username that could not find author by") {
            // region setup
            every {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, compositionSourceId, authorId
                )
            } throws CompositionException(FailedToFindAuthorByUsername)
            // endregion

            val ex = shouldThrowExactly<CompositionException> {
                carouselOfImagesManager.createComposition(createPublicCarouselBasicImagesReq, layoutId, authorId)
            }
            verify { appEnv.database.useTransaction { } }

            ex.code shouldBe FailedToFindAuthorByUsername
        }

        xthen("provided a layoutId that is restricted") {

        }

        xthen("provided a layoutId that does not exist") {

        }

        then("success") {
            val res = carouselOfImagesManager.createComposition(createPublicCarouselBasicImagesReq, layoutId, authorId)

            verify { imageRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesReq.images) }
            verify { textRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesReq.imgOnclickRedirects) }
            verify {
                compositionPrivilegesManager.createCompositionSource(
                    compositionType = 0,
                    privilegeLevel = 0,
                    name = "my composition",
                    authorId = authorId,
                )
            }
            verify {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, compositionSourceId, authorId
                )
            }
            verify { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared) }

            res shouldBe compositionSourceId
        }
    }
})
