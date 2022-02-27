package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.exceptions.CompositionCode.IdOfRecordProvidedNotOfComposition
import com.idealIntent.exceptions.CompositionCode.ModifyPermittedToAuthorOfCompositionNotFound
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.CompositionTypeManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfCarouselOfImages.*
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CompositionCarouselType

class CarouselOfImagesManager(
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionSourceRepository: CompositionSourceRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
    private val spaceRepository: SpaceRepository,
) : CompositionTypeManagerStructure<CarouselBasicImagesRes, IImagesCarouselEntity, CreateCarouselBasicImagesReq,
        CarouselOfImagesComposePrepared, CompositionResponse>() {

    override fun getPublicComposition(compositionSourceId: Int): CarouselBasicImagesRes? =
        carouselOfImagesRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): CarouselBasicImagesRes? =
        carouselOfImagesRepository.getPrivateComposition(compositionSourceId, authorId)

    override fun createComposition(
        createRequest: CreateCarouselBasicImagesReq,
        layoutId: Int,
        authorId: Int
    ): Int {
        appEnv.database.useTransaction {
            val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
            val redirectsCollectionId =
                textRepository.batchInsertRecordsToNewCollection(createRequest.imgOnclickRedirects)

            val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
                compositionType = CompositionCarouselType.BasicImages.value,
                privilegeLevel = createRequest.privilegeLevel,
                name = createRequest.name,
                authorId = authorId,
            )

            spaceRepository.associateCompositionToLayout(
                orderRank = 0,
                compositionSourceId = compositionSourceId,
                layoutId = layoutId
            )

            carouselOfImagesRepository.compose(
                CarouselOfImagesComposePrepared(
                    name = createRequest.name,
                    imageCollectionId = imageCollectionId,
                    redirectTextCollectionId = redirectsCollectionId,
                    sourceId = compositionSourceId,
                ),
                sourceId = compositionSourceId
            )

            compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                createRequest.privilegedAuthors, compositionSourceId, authorId
            )

            return compositionSourceId
        }
    }

    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) {
        with(carouselOfImagesRepository) {
            val (sourceId, id, name, imageCollectionId, redirectTextCollectionId) =
                getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId,
                    authorId = authorId
                ) ?: throw CompositionException(ModifyPermittedToAuthorOfCompositionNotFound)
            val gson = Gson()

            compositionUpdateQue.forEach {
                val record = it.recordUpdate

                when (UpdateDataOfCarouselOfImages.fromInt(it.updateDataOf)) {
                    Image -> {
                        if (!imageRepository.validateRecordToCollectionRelationship(record.recordId, imageCollectionId))
                            throw CompositionException(
                                IdOfRecordProvidedNotOfComposition,
                                gson.toJson("Image id ${record.recordId}")
                            )
                        else imageRepository.updateRecord(record)
                    }
                    RedirectText -> {
                        if (!textRepository.validateRecordToCollectionRelationship(
                                record.recordId, redirectTextCollectionId
                            )
                        ) throw CompositionException(
                            IdOfRecordProvidedNotOfComposition,
                            gson.toJson("Text id ${record.recordId}")
                        )
                        else textRepository.updateRecord(record)
                    }
                    CompositionName -> {
                        compositionSourceRepository.renameComposition(record.updateTo[0].value)
                    }
                }
            }
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) =
        carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId)
}