package com.idealIntent.managers.compositions.grids

import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.grids.GridOneOffComposePrepared
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.dtos.compositions.grids.GridOneOffRes
import com.idealIntent.dtos.compositions.grids.GridOneOffRes3
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.CompositionTypeManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.managers.compositions.images.D2ImageRepository
import com.idealIntent.managers.compositions.texts.D2TextRepository
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.grids.GridOneOffRepository
import dtos.compositions.carousels.CompositionCarouselType

class GridOneOffManager(
    private val textRepository: TextRepository,
    private val gridOneOffRepository: GridOneOffRepository,
    private val spaceRepository: SpaceRepository,
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
    private val compositionSourceRepository: CompositionSourceRepository,
    private val imageRepository: ImageRepository,
    private val image2dManager: D2ImageRepository,
    private val text2dManager: D2TextRepository,
) : CompositionTypeManagerStructure<GridOneOffRes3, IImagesCarouselEntity, GridOneOffCreateReq, CarouselOfImagesComposePrepared,
        CompositionResponse>() {

    override fun getPublicComposition(compositionSourceId: Int): GridOneOffRes3? =
        gridOneOffRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): GridOneOffRes3? =
        gridOneOffRepository.getPrivateComposition(compositionSourceId, authorId)

    override fun createComposition(
        createRequest: GridOneOffCreateReq,
        layoutId: Int,
        authorId: Int
    ): Int = appEnv.database.useTransaction {
        val titlesOfImagesId =
            textRepository.batchInsertRecordsToNewCollection(createRequest.collectionOf_titles_of_image_categories)
        val image2dCollectionId =
            image2dManager.batchInsertRecordsToNewCollection(createRequest.collectionOf_images_2d)
        val redirects2dCollectionId =
            text2dManager.batchInsertRecordsToNewCollection(createRequest.collectionOf_onclick_redirects)
        val imgDescriptions2dId =
            text2dManager.batchInsertRecordsToNewCollection(createRequest.collectionOf_img_descriptions)

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

        gridOneOffRepository.compose(
            composePrepared = GridOneOffComposePrepared(
                sourceId = compositionSourceId,
                collectionOf_titles_of_image_categories_id = titlesOfImagesId,
                collectionOf_images_2d_id = image2dCollectionId,
                collectionOf_onclick_redirects_id = redirects2dCollectionId,
                collectionOf_img_descriptions_id = imgDescriptions2dId,
            ),
            sourceId = compositionSourceId
        )

        compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
            createRequest.privilegedAuthors, compositionSourceId, authorId
        )

        return compositionSourceId
    }

    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) = with(gridOneOffRepository) {
        TODO()
//        val (sourceId, id, name) = getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//            compositionSourceId = compositionSourceId, authorId = authorId
//        ) ?: throw CompositionException(CompositionCode.ModifyPermittedToAuthorOfCompositionNotFound)
//
//        compositionUpdateQue.forEach {
//            val record = it.recordUpdate
//
//            when (UpdateDataOfCarouselOfImages.fromInt(it.updateDataOf)) {
//                UpdateDataOfCarouselOfImages.Image -> {
//                    if (!imageRepository.validateRecordToCollectionRelationship(record.recordId, collectionId))
//                        throw CompositionException(
//                            CompositionCode.IdOfRecordProvidedNotOfComposition,
//                            gson.toJson("Image id ${record.recordId}")
//                        )
//                    else imageRepository.updateRecord(record)
//                }
//                UpdateDataOfCarouselOfImages.RedirectText -> {
//                    if (!textRepository.validateRecordToCollectionRelationship(
//                            record.recordId, redirectTextCollectionId
//                        )
//                    ) throw CompositionException(
//                        CompositionCode.IdOfRecordProvidedNotOfComposition,
//                        gson.toJson("Text id ${record.recordId}")
//                    )
//                    else textRepository.updateRecord(record)
//                }
//                UpdateDataOfCarouselOfImages.CompositionName -> {
//                    compositionSourceRepository.renameComposition(record.updateTo[0].value)
//                }
//            }
//        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) =
        gridOneOffRepository.deleteComposition(compositionSourceId, authorId)
}