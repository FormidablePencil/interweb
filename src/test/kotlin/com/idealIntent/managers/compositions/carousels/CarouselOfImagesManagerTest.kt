package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositionCRUD.UpdateColumn
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.dtos.compositions.carousels.ImagesCarouselTopLvlIds
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionCode.FailedToFindAuthorByUsername
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.collectionsGeneric.images.ImagesCOL
import dtos.collectionsGeneric.texts.TextsCOL
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import shared.appEnvMockHelper
import shared.testUtils.carouselBasicImagesRes
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
    val createPublicCarouselBasicImagesRequest = CreateCarouselBasicImagesReq(
        name = createPublicCarouselBasicImagesReq.name,
        images = createPublicCarouselBasicImagesReq.images,
        imgOnclickRedirects = createPublicCarouselBasicImagesReq.imgOnclickRedirects,
        privilegedAuthors = privilegedAuthors,
        privilegeLevel = 0,
    )

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

    given("getPublicComposition") {
        every {
            carouselOfImagesRepository.getPublicComposition(compositionSourceId)
        } returns carouselBasicImagesRes

        carouselOfImagesManager.getPublicComposition(compositionSourceId) shouldBe carouselBasicImagesRes
    }

    given("getPrivateComposition") {
        every {
            carouselOfImagesRepository.getPrivateComposition(compositionSourceId, authorId)
        } returns carouselBasicImagesRes

        carouselOfImagesManager.getPrivateComposition(compositionSourceId, authorId) shouldBe carouselBasicImagesRes
    }

    given("createComposition") {
        beforeEach {
            every { imageRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesRequest.images) } returns
                    carouselOfImagesComposePrepared.imageCollectionId
            every { textRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesRequest.imgOnclickRedirects) } returns
                    carouselOfImagesComposePrepared.redirectTextCollectionId
            every {
                compositionPrivilegesManager.createCompositionSource(
                    compositionType = 0,
                    privilegeLevel = createPublicCarouselBasicImagesRequest.privilegeLevel,
                    name = createPublicCarouselBasicImagesRequest.name,
                    authorId = authorId
                )
            } returns
                    carouselOfImagesComposePrepared.sourceId
            justRun {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, compositionSourceId, authorId
                )
            }
            every { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared, compositionSourceId) } returns compositionSourceId
            every {
                spaceRepository.associateCompositionToLayout(
                    orderRank = 0,
                    compositionSourceId = compositionSourceId,
                    layoutId = layoutId
                )
            } returns true
        }

        then("provided a username that could not find author by id") {
            // region setup
            every {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, compositionSourceId, authorId
                )
            } throws CompositionException(FailedToFindAuthorByUsername)
            // endregion

            shouldThrowExactly<CompositionException> {
                carouselOfImagesManager.createComposition(createPublicCarouselBasicImagesRequest, layoutId, authorId)
            }.code shouldBe FailedToFindAuthorByUsername
            verify { appEnv.database.useTransaction { } }
        }

        then("successfully created") {
            val res = carouselOfImagesManager.createComposition(
                createPublicCarouselBasicImagesRequest, layoutId, authorId
            )

            verify { imageRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesRequest.images) }
            verify { textRepository.batchInsertRecordsToNewCollection(createPublicCarouselBasicImagesRequest.imgOnclickRedirects) }
            verify {
                compositionPrivilegesManager.createCompositionSource(
                    compositionType = 0,
                    privilegeLevel = 0,
                    name = createPublicCarouselBasicImagesRequest.name,
                    authorId = authorId,
                )
            }
            verify {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    createPublicCarouselBasicImagesRequest.privilegedAuthors, compositionSourceId, authorId
                )
            }
            verify { carouselOfImagesRepository.compose(carouselOfImagesComposePrepared, compositionSourceId) }

            res shouldBe compositionSourceId
        }
    }

    given("updateComposition") {
        val imagesCarouselTopLvlIds = ImagesCarouselTopLvlIds(
            sourceId = compositionSourceId,
            id = 321,
            name = "name",
            imageCollectionId = 765,
            redirectTextCollectionId = 567,
        )

        and("Image") {

            then("recordId provided not of imageCollection in composition") {
                // region Setup
                val updateTextDataOfComposition = UpdateDataOfComposition(
                    updateDataOf = UpdateDataOfCarouselOfImages.Image.value,
                    recordUpdate = RecordUpdate(
                        recordId = 123,
                        updateTo = listOf(UpdateColumn(column = ImagesCOL.Url.value, value = "hakuna matata"))
                    )
                )
                val compositionUpdateQue = listOf(updateTextDataOfComposition)

                every {
                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId, authorId
                    )
                } returns imagesCarouselTopLvlIds
                every {
                    imageRepository.validateRecordToCollectionRelationship(
                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
                        collectionId = imagesCarouselTopLvlIds.imageCollectionId
                    )
                } returns false
                // endregion Setup

                shouldThrowExactly<CompositionException> {
                    carouselOfImagesManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
                }.code shouldBe CompositionCode.IdOfRecordProvidedNotOfComposition
            }

            then("successfully updated image") {
                // region Setup
                val updateTextDataOfComposition = UpdateDataOfComposition(
                    updateDataOf = UpdateDataOfCarouselOfImages.Image.value,
                    recordUpdate = RecordUpdate(
                        recordId = 123,
                        updateTo = listOf(UpdateColumn(column = ImagesCOL.Url.value, value = "hakuna matata"))
                    )
                )
                val compositionUpdateQue = listOf(updateTextDataOfComposition)

                every {
                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId, authorId
                    )
                } returns imagesCarouselTopLvlIds
                every {
                    imageRepository.validateRecordToCollectionRelationship(
                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
                        collectionId = imagesCarouselTopLvlIds.imageCollectionId
                    )
                } returns true
                justRun { imageRepository.updateRecord(updateTextDataOfComposition.recordUpdate) }
                // endregion Setup

                carouselOfImagesManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
            }
        }

        and("RedirectText") {

            then("recordId provided not of textCollection in composition") {
                // region Setup
                val updateTextDataOfComposition = UpdateDataOfComposition(
                    updateDataOf = UpdateDataOfCarouselOfImages.Image.value,
                    recordUpdate = RecordUpdate(
                        recordId = 123,
                        updateTo = listOf(UpdateColumn(column = ImagesCOL.Url.value, value = "hakuna matata"))
                    )
                )
                val compositionUpdateQue = listOf(updateTextDataOfComposition)

                every {
                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId, authorId
                    )
                } returns imagesCarouselTopLvlIds
                every {
                    imageRepository.validateRecordToCollectionRelationship(
                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
                        collectionId = imagesCarouselTopLvlIds.imageCollectionId
                    )
                } returns false
                justRun { imageRepository.updateRecord(updateTextDataOfComposition.recordUpdate) }
                // endregion Setup

                shouldThrowExactly<CompositionException> {
                    carouselOfImagesManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
                }.code shouldBe CompositionCode.IdOfRecordProvidedNotOfComposition
            }

            then("successfully updated text") {
                // region Setup
                val updateTextDataOfComposition = UpdateDataOfComposition(
                    updateDataOf = UpdateDataOfCarouselOfImages.RedirectText.value,
                    recordUpdate = RecordUpdate(
                        recordId = 123,
                        updateTo = listOf(UpdateColumn(column = TextsCOL.Text.value, value = "hakuna matata"))
                    )
                )
                val compositionUpdateQue = listOf(updateTextDataOfComposition)

                every {
                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId, authorId
                    )
                } returns imagesCarouselTopLvlIds
                every {
                    textRepository.validateRecordToCollectionRelationship(
                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
                        collectionId = imagesCarouselTopLvlIds.redirectTextCollectionId
                    )
                } returns true
                justRun { textRepository.updateRecord(updateTextDataOfComposition.recordUpdate) }
                // endregion Setup

                carouselOfImagesManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
            }
        }

        and("CompositionName") {

            then("successfully updated name of composition") {
                // region Setup
                val updateTextDataOfComposition = UpdateDataOfComposition(
                    updateDataOf = UpdateDataOfCarouselOfImages.CompositionName.value,
                    recordUpdate = RecordUpdate(
                        recordId = 123,
                        updateTo = listOf(UpdateColumn(column = 0, value = "hakuna matata"))
                    )
                )
                every {
                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId, authorId
                    )
                } returns imagesCarouselTopLvlIds
                every {
                    compositionSourceRepository.renameComposition(
                        updateTextDataOfComposition.recordUpdate.updateTo[0].value
                    )
                } returns true
                val compositionUpdateQue = listOf(updateTextDataOfComposition)
                // endregion

                carouselOfImagesManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
            }
        }
    }

    given("deleteComposition") {

        then("successfully deleted") {
            justRun { carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId) }

            carouselOfImagesManager.deleteComposition(compositionSourceId, authorId)
        }
    }
})
