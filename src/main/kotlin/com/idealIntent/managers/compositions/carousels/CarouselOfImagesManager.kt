package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionManagerStructure
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CarouselOfImagesTABLE
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CarouselOfImagesManager(
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) : ICompositionManagerStructure<CreateCarouselBasicImagesReq, IImagesCarouselEntity,
        CreateCarouselBasicImagesReq, CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    val appEnv: AppEnv by inject()

    // region Get
    /**
     * Get composition
     *
     * Validate that user is privileged first returning composition.
     */

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
    override fun createComposition(createRequest: CreateCarouselBasicImagesReq, authorId: Int): CompositionResponse {
        try {
            appEnv.database.useTransaction {
                val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                val redirectsCollectionId =
                    textRepository.batchInsertRecordsToNewCollection(createRequest.imgOnclickRedirects)

                val privilegeSourceId = compositionPrivilegesManager.createPrivileges(authorId)
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    createRequest.privilegedAuthors, privilegeSourceId, authorId
                )

                val compositionId = carouselOfImagesRepository.compose(
                    CarouselOfImagesComposePrepared(
                        name = createRequest.name,
                        imageCollectionId = imageCollectionId,
                        redirectTextCollectionId = redirectsCollectionId,
                        privilegeId = privilegeSourceId,
                    )
                ) ?: throw CompositionExceptionReport(FailedToCompose, this::class.java)

                return CompositionResponse().succeeded(HttpStatusCode.Created, compositionId)
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
    fun deleteComposition(id: Int): Boolean {
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
//            CarouselOfImagesTABLE.Images ->
//                imageRepository.updateRecord(updateToData,, componentId)
//            CarouselOfImagesTABLE.NavTos ->
//                textRepository.updateRecord(updateToData,, componentId)
//            CarouselOfImagesTABLE.Privileges ->
//                compositionPrivilegesRepository.updateRecord(updateToData,, componentId)
//        }
    }


    fun insertCompositions(components: List<CreateCarouselBasicImagesReq>, label: String): Int? {
        TODO("Not yet implemented")
    }

    override fun updateComposition(id: Int, record: RecordUpdate): Boolean {
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