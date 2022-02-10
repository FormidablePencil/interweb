package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.ICompositionTypeManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfCarouselOfImages.*
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CompositionCarouselType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CarouselOfImagesManager(
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionSourceRepository: CompositionSourceRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
    private val spaceRepository: SpaceRepository,
) : ICompositionTypeManagerStructure<CarouselBasicImagesRes, IImagesCarouselEntity, CreateCarouselBasicImagesReq,
        CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    val appEnv: AppEnv by inject()

    override fun getPublicComposition(compositionSourceId: Int): CarouselBasicImagesRes? =
        carouselOfImagesRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): CarouselBasicImagesRes? =
        carouselOfImagesRepository.getPrivateComposition(compositionSourceId, authorId)

    /**
     * Insert images and redirection texts, create a collection for each, create an association between image and
     * assign privileges compositions to specified authors. If either looking up author by id or assigning privileges to
     * authors fails then return a response a fail response to client with the author's username that failed.
     * Otherwise, if all went well, pass ids of image's and redirection text's collections to
     * [compose][CarouselOfImagesRepository.compose]. Then return id of the newly created composition.
     *
     * @see ICompositionTypeManagerStructure.createComposition
     * @return Id of the newly created composition.
     * @throws CompositionException [FailedToFindAuthorByUsername]
     */
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

            // todo wrap in a try catch and response to user that layout by id does not exist
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
                )
            ) ?: throw CompositionExceptionReport(FailedToCompose, this::class.java)

            compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                createRequest.privilegedAuthors, compositionSourceId, authorId
            )

            return compositionSourceId
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId)
    }

    /**
     * Update composition
     *
     * First validate that the author is privileged to update a composition of source id provided. Then transforms provided
     * value from json to a type object corresponding to [UpdateDataOfCarouselOfImages]. Validate that the id of
     * item to update to is of the id of the composition source provided. Then update property of composition.
     *
     * @param compositionUpdateQue Id of composition source of composition to do an update on, what to update and
     * the value to update to.
     * @throws CompositionException [ModifyPermittedToAuthorOfCompositionNotFound], [IdOfRecordProvidedNotOfComposition].
     */
    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) {
        with(carouselOfImagesRepository) {
            val (sourceId, id, name, imageCollectionId, redirectTextCollectionId) = getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
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
                        else imageRepository.updateRecord(record, imageCollectionId)
                    }
                    RedirectText -> {
                        if (!textRepository.validateRecordToCollectionRelationship(
                                record.recordId,
                                redirectTextCollectionId
                            )
                        )
                            throw CompositionException(
                                IdOfRecordProvidedNotOfComposition,
                                gson.toJson("Text id ${record.recordId}")
                            )
                        else textRepository.updateRecord(record, imageCollectionId)
                    }
                    CompositionName -> {
                        compositionSourceRepository.renameComposition(record.updateTo[0].value)
                    }
                }
            }
        }
    }
}