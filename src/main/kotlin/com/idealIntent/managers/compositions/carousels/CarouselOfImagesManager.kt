package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesReq
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
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
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) : ICompositionManagerStructure<CarouselBasicImagesReq, IImagesCarouselEntity,
        CarouselBasicImagesReq, CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    val appEnv: AppEnv by inject()
    // todo - there will be multiple kinds of carousels thus will be a component of a component some day

    // region Get
    fun getMetadataOfComposition(id: Int): IImagesCarouselEntity {
        TODO()
    }
    // endregion Get

    // region Insert
    override fun createComposition(createRequest: CarouselBasicImagesReq): CompositionResponse {
        try {
            appEnv.database.useTransaction {
                // todo - handle transaction throw exception
                val (_, imageCollectionId) = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                    ?: run {
                        it.rollback();
                        return CompositionResponse().failed(CompositionCode.FailedToInsertRecord, "images.")
                    }

                val (_, redirectsCollectionId) = textRepository.batchInsertRecordsToNewCollection(createRequest.imgOnclickRedirects)
                    ?: run {
                        it.rollback();
                        return CompositionResponse().failed(CompositionCode.FailedToInsertRecord, "redirects.")
                    }

                val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()

                val (hasFailed, hasFailedAtAuthorLookup, username) = compositionPrivilegesRepository.giveMultipleAuthorsPrivilegesByUsername(
                    createRequest.privilegedAuthors, privilegeSourceId
                )
                if (hasFailed) {
                    it.rollback()
                    return if (hasFailedAtAuthorLookup)
                        CompositionResponse().failed(CompositionCode.FailedToGivePrivilege, username)
                    else CompositionResponse().failed(CompositionCode.FailedAtAuthorLookup, username)
                }

                val compositionId = carouselOfImagesRepository.compose(
                    CarouselOfImagesComposePrepared(
                        name = createRequest.name,
                        imageCollectionId = imageCollectionId,
                        redirectTextCollectionId = redirectsCollectionId,
                        privilegeId = privilegeSourceId,
                    )
                ) ?: throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)

                return CompositionResponse().succeeded(HttpStatusCode.Created, compositionId)
            }
        } catch (ex: CompositionException) {
            ex.message
            return when (ex.code) {
                CompositionCode.FailedToInsertRecord -> CompositionResponse().failed(CompositionCode.FailedToInsertRecord)
                CompositionCode.FailedToFindAuthor -> CompositionResponse().failed(CompositionCode.FailedToFindAuthor)
                else -> throw CompositionExceptionReport(CompositionCode.ServerError, this::class.java)
            }
        }
    }
    // endregion Insert

    // region Delete

    // endregion Delete


    // region CarouselOfImages

    // todo - if multiple comps in one repository then which one to delete?
    // CarouselOfImagesRepository only for sql queries concerning all carousel components
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


    fun insertCompositions(components: List<CarouselBasicImagesReq>, label: String): Int? {
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