package com.idealIntent.managers.compositions.banners

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.banners.BannerImageRepository
import dtos.compositions.banners.CompositionBanner
import integrationTests.compositions.banners.BannerCompositionsFlow.Companion.publicBannerImageCreateReq
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import shared.appEnvMockHelper

class BannerImageManagerTest : BehaviorSpec({
    val compositionPrivilegesManager: CompositionPrivilegesManager = mockk()
    val bannerImageRepository: BannerImageRepository = mockk()
    val spaceRepository: SpaceRepository = mockk()
    val appEnv: AppEnv = mockk()

    val authorId = 1
    val compositionSourceId = 89
    val layoutId = 54

    val bannerImageManager = spyk(
        BannerImageManager(
            bannerImageRepository = bannerImageRepository,
            compositionPrivilegesManager = compositionPrivilegesManager,
            spaceRepository = spaceRepository,
        )
    )

    val bannerImageRes = publicBannerImageCreateReq.let {
        BannerImageRes(
            sourceId = compositionSourceId,
            compositionId = 123,
            imageUrl = it.imageUrl,
            imageAlt = it.imageAlt,
            privilegeLevel = it.privilegeLevel,
            name = it.name,
            privilegedAuthors = it.privilegedAuthors,
        )
    }

    beforeEach {
        clearAllMocks()
        appEnvMockHelper(appEnv, bannerImageManager)
    }

    given("getPublicComposition") {
        every {
            bannerImageRepository.getPublicComposition(compositionSourceId)
        } returns bannerImageRes

        bannerImageManager.getPublicComposition(compositionSourceId) shouldBe bannerImageRes
    }

    given("getPrivateComposition") {
        every {
            bannerImageRepository.getPrivateComposition(compositionSourceId, authorId)
        } returns bannerImageRes

        bannerImageManager.getPrivateComposition(compositionSourceId, authorId) shouldBe bannerImageRes
    }

    given("createComposition") {
        beforeEach {
            every {
                compositionPrivilegesManager.createCompositionSource(
                    compositionType = CompositionBanner.Basic.value,
                    privilegeLevel = publicBannerImageCreateReq.privilegeLevel,
                    name = publicBannerImageCreateReq.name,
                    authorId = authorId
                )
            } returns compositionSourceId
            justRun {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    publicBannerImageCreateReq.privilegedAuthors, compositionSourceId, authorId
                )
            }
            every {
                bannerImageRepository.compose(
                    publicBannerImageCreateReq,
                    compositionSourceId
                )
            } returns compositionSourceId
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
                    publicBannerImageCreateReq.privilegedAuthors, compositionSourceId, authorId
                )
            } throws CompositionException(CompositionCode.FailedToFindAuthorByUsername)
            // endregion

            shouldThrowExactly<CompositionException> {
                bannerImageManager.createComposition(publicBannerImageCreateReq, layoutId, authorId)
            }.code shouldBe CompositionCode.FailedToFindAuthorByUsername
            verify { appEnv.database.useTransaction { } }
        }

        then("successfully created") {
            val res = bannerImageManager.createComposition(publicBannerImageCreateReq, layoutId, authorId)

            verify {
                compositionPrivilegesManager.createCompositionSource(
                    compositionType = CompositionBanner.Basic.value,
                    privilegeLevel = publicBannerImageCreateReq.privilegeLevel,
                    name = publicBannerImageCreateReq.name,
                    authorId = authorId,
                )
            }
            verify {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    publicBannerImageCreateReq.privilegedAuthors, compositionSourceId, authorId
                )
            }
            verify { bannerImageRepository.compose(publicBannerImageCreateReq, compositionSourceId) }

            res shouldBe compositionSourceId
        }
    }

    xgiven("updateComposition") {
//        val imagesCarouselTopLvlIds = ImagesCarouselTopLvlIds(
//            sourceId = compositionSourceId,
//            id = 321,
//            name = "name",
//            imageCollectionId = 765,
//            redirectTextCollectionId = 567,
//        )
//
//        and("Image") {
//
//            then("recordId provided not of imageCollection in composition") {
//                // region Setup
//                val updateTextDataOfComposition = UpdateDataOfComposition(
//                    updateDataOf = UpdateDataOfCarouselOfImages.Image.value,
//                    recordUpdate = RecordUpdate(
//                        recordId = 123,
//                        updateTo = listOf(UpdateColumn(column = ImagesCOL.Url.value, value = "hakuna matata"))
//                    )
//                )
//                val compositionUpdateQue = listOf(updateTextDataOfComposition)
//
//                every {
//                    bannerImageRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//                        compositionSourceId, authorId
//                    )
//                } returns imagesCarouselTopLvlIds
//                every {
//                    imageRepository.validateRecordToCollectionRelationship(
//                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
//                        collectionId = imagesCarouselTopLvlIds.imageCollectionId
//                    )
//                } returns false
//                // endregion Setup
//
//                shouldThrowExactly<CompositionException> {
//                    bannerImageManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
//                }.code shouldBe CompositionCode.IdOfRecordProvidedNotOfComposition
//            }
//
//            then("successfully updated imageUrl") {
//                // region Setup
//                val updateTextDataOfComposition = UpdateDataOfComposition(
//                    updateDataOf = UpdateDataOfCarouselOfImages.Image.value,
//                    recordUpdate = RecordUpdate(
//                        recordId = 123,
//                        updateTo = listOf(UpdateColumn(column = ImagesCOL.Url.value, value = "hakuna matata"))
//                    )
//                )
//                val compositionUpdateQue = listOf(updateTextDataOfComposition)
//
//                every {
//                    bannerImageRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//                        compositionSourceId, authorId
//                    )
//                } returns imagesCarouselTopLvlIds
//                every {
//                    imageRepository.validateRecordToCollectionRelationship(
//                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
//                        collectionId = imagesCarouselTopLvlIds.imageCollectionId
//                    )
//                } returns true
//                justRun { imageRepository.updateRecord(updateTextDataOfComposition.recordUpdate) }
//                // endregion Setup
//
//                bannerImageManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
//            }
//        }
//
//        and("RedirectText") {
//
//            then("recordId provided not of textCollection in composition") {
//                // region Setup
//                val updateTextDataOfComposition = UpdateDataOfComposition(
//                    updateDataOf = UpdateDataOfCarouselOfImages.Image.value,
//                    recordUpdate = RecordUpdate(
//                        recordId = 123,
//                        updateTo = listOf(UpdateColumn(column = ImagesCOL.Url.value, value = "hakuna matata"))
//                    )
//                )
//                val compositionUpdateQue = listOf(updateTextDataOfComposition)
//
//                every {
//                    bannerImageRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//                        compositionSourceId, authorId
//                    )
//                } returns imagesCarouselTopLvlIds
//                every {
//                    imageRepository.validateRecordToCollectionRelationship(
//                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
//                        collectionId = imagesCarouselTopLvlIds.imageCollectionId
//                    )
//                } returns false
//                justRun { imageRepository.updateRecord(updateTextDataOfComposition.recordUpdate) }
//                // endregion Setup
//
//                shouldThrowExactly<CompositionException> {
//                    bannerImageManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
//                }.code shouldBe CompositionCode.IdOfRecordProvidedNotOfComposition
//            }
//
//            then("successfully updated text") {
//                // region Setup
//                val updateTextDataOfComposition = UpdateDataOfComposition(
//                    updateDataOf = UpdateDataOfCarouselOfImages.RedirectText.value,
//                    recordUpdate = RecordUpdate(
//                        recordId = 123,
//                        updateTo = listOf(UpdateColumn(column = TextsCOL.Text.value, value = "hakuna matata"))
//                    )
//                )
//                val compositionUpdateQue = listOf(updateTextDataOfComposition)
//
//                every {
//                    bannerImageRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//                        compositionSourceId, authorId
//                    )
//                } returns imagesCarouselTopLvlIds
//                every {
//                    textRepository.validateRecordToCollectionRelationship(
//                        recordId = updateTextDataOfComposition.recordUpdate.recordId,
//                        collectionId = imagesCarouselTopLvlIds.redirectTextCollectionId
//                    )
//                } returns true
//                justRun { textRepository.updateRecord(updateTextDataOfComposition.recordUpdate) }
//                // endregion Setup
//
//                bannerImageManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
//            }
//        }
//
//        and("CompositionName") {
//
//            then("successfully updated name of composition") {
//                // region Setup
//                val updateTextDataOfComposition = UpdateDataOfComposition(
//                    updateDataOf = UpdateDataOfCarouselOfImages.CompositionName.value,
//                    recordUpdate = RecordUpdate(
//                        recordId = 123,
//                        updateTo = listOf(UpdateColumn(column = 0, value = "hakuna matata"))
//                    )
//                )
//                every {
//                    bannerImageRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//                        compositionSourceId, authorId
//                    )
//                } returns imagesCarouselTopLvlIds
//                every {
//                    compositionSourceRepository.renameComposition(
//                        updateTextDataOfComposition.recordUpdate.updateTo[0].value
//                    )
//                } returns true
//                val compositionUpdateQue = listOf(updateTextDataOfComposition)
//                // endregion
//
//                bannerImageManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
//            }
//        }
    }

    xgiven("deleteComposition") {

        then("successfully deleted") {
            justRun { bannerImageRepository.deleteComposition(compositionSourceId, authorId) }

            bannerImageManager.deleteComposition(compositionSourceId, authorId)
        }
    }
})
