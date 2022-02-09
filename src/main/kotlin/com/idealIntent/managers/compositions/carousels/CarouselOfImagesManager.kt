package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.carousels.UpdateDataOfCarouselOfImages.*
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionManagerStructure
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CarouselOfImagesTABLE
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

//List<Pair<UpdateDataOfCarouselOfImages, RecordUpdate>>

class CarouselOfImagesManager(
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
    private val spaceRepository: SpaceRepository,
) : ICompositionManagerStructure<CarouselBasicImagesRes, IImagesCarouselEntity, CreateCarouselBasicImagesReq,
        CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    val appEnv: AppEnv by inject()

    // region Get
    /**
     * Get composition
     *
     * Validate that user is privileged first returning composition.
     */

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): CarouselBasicImagesRes? =
        carouselOfImagesRepository.getPrivateComposition(compositionSourceId, authorId)

    // endregion Get

    // region Insert

    /**
     * Insert images and redirection texts, create a collection for each, create an association between image and
     * assign privileges compositions to specified authors. If either looking up author by id or assigning privileges to
     * authors fails then return a response a fail response to client with the author's username that failed.
     * Otherwise, if all went well, pass ids of image's and redirection text's collections to
     * [compose][CarouselOfImagesRepository.compose]. Then return id of the newly created composition.
     *
     * @see ICompositionManagerStructure.createComposition
     * @return Response of id of the newly created composition or failed response of [FailedToFindAuthorByUsername].
     */
    override fun createComposition(
        createRequest: CreateCarouselBasicImagesReq,
        layoutId: Int,
        authorId: Int
    ): CompositionResponse {
        try {
            appEnv.database.useTransaction {
                val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                val redirectsCollectionId =
                    textRepository.batchInsertRecordsToNewCollection(createRequest.imgOnclickRedirects)

                val compositionSourceId =
                    compositionPrivilegesManager.createCompositionSource(compositionType = 0, authorId)

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

                return CompositionResponse().succeeded(HttpStatusCode.Created, compositionSourceId)
            }
        } catch (ex: CompositionException) {
            when (ex.code) {
                FailedToFindAuthorByUsername ->
                    return CompositionResponse().failed(ex.code, ex.moreDetails)
                else ->
                    throw CompositionExceptionReport(ServerError, this::class.java, ex)

            }
        }
    }
    // endregion Insert

    // region Delete

    // endregion Delete


    // region CarouselOfImages

    // Everything else will be extracted into managers. SpaceResponseFailed manager for each component of category (e.g. carousel.CarouselBasicImages)
    override fun deleteComposition(compositionSourceId: Int, authorId: Int): Boolean {
        carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId)
        TODO()
//        return database.imagesCarousels.removeIf { it.id eq id } != 0
    }

    // region update
    // endregion update

    fun batchUpdate(componentId: Int, table: CarouselOfImagesTABLE, updateToData: List<RecordUpdate>) {
        when (table) {
            CarouselOfImagesTABLE.Images ->
                imageRepository.batchUpdateRecords(updateToData, componentId)
            CarouselOfImagesTABLE.NavTos ->
                textRepository.batchUpdateRecords(updateToData, componentId)
            CarouselOfImagesTABLE.Privileges ->
                compositionPrivilegesRepository.batchUpdateRecords(updateToData, componentId)
        }
    }

    fun update(componentId: Int, column: CarouselOfImagesTABLE, updateToData: RecordUpdate) {
        TODO()
//        when (column) {
//        CarouselOfImagesTABLEOfImagesTABLE.Images ->
//                imageRepository.updateRecord(updateToData,, componentId)
//            CarouselOfImagesTABLE.NavTos ->
//                textRepository.updateRecord(updateToData,, componentId)
//            CarouselOfImagesTABLE.Privileges ->
//                compositionPrivilegesRepository.updateRecord(updateToData,, componentId)
//        }
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
     * @throws CompositionException [ModifyPermittedToAuthorOfCompositionNotFound]
     */
    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) {
        with(carouselOfImagesRepository) {
            val (sourceId, id, name, imageCollectionId, redirectTextCollectionId) = getOnlyTopLvlIdsOfCompositionOnlyModifiable(
                onlyModifiable = true,
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
                        if (!textRepository.validateRecordToCollectionRelationship(record.recordId, imageCollectionId))
                            throw CompositionException(
                                IdOfRecordProvidedNotOfComposition,
                                gson.toJson("Text id ${record.recordId}")
                            )
                        else textRepository.updateRecord(record, imageCollectionId)
                    }
                    PrivilegedAuthor ->
                        TODO("CompositionPrivilegesRepository.update")

                    CompositionName ->
                        TODO()
                }
            }
        }
    }

    // region Validate id of composition property to be of composition


    fun insertCompositions(components: List<CreateCarouselBasicImagesReq>, label: String): Int? {
        TODO("Not yet implemented")
    }

    override fun batchUpdateCompositions(id: Int, records: List<RecordUpdate>): Boolean {
        TODO("Not yet implemented")
    }

    fun batchDeleteCompositions(id: Int): Boolean {
        TODO("Not yet implemented")
    }
    // endregion
}

enum class UpdateDataOfCarouselOfImages(val value: Int) {
    Image(0),
    RedirectText(1),
    PrivilegedAuthor(2),
    CompositionName(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}