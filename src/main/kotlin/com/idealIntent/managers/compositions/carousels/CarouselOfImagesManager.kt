package com.idealIntent.managers.compositions.carousels

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthorsToComposition
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesReq
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionsGenericPrivileges
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionManagerStructure
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.repositories.profile.AuthorRepository
import dtos.compositions.carousels.CarouselOfImagesTABLE
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.dsl.*

class CarouselOfImagesManager(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository,
    private val authorRepository: AuthorRepository,
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) : ICompositionManagerStructure<CarouselBasicImagesReq, IImagesCarouselEntity,
        CarouselBasicImagesReq, CarouselOfImagesComposePrepared>, KoinComponent {
    val appEnv: AppEnv by inject()
    // todo - there will be multiple kinds of carousels thus will be a component of a component some day

    // region Get
    fun getMetadataOfComposition(id: Int): IImagesCarouselEntity {
        TODO()
    }
    // endregion Get

    // region Insert
    override fun createComposition(createRequest: CarouselBasicImagesReq): Int? {
        appEnv.database.useTransaction { // todo - handle transaction throw exception
            val (_, imageCollectionId) = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                ?: TODO("failed")
            val (_, redirectsCollectionId) = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                ?: TODO("failed")

            val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
            createRequest.privilegedAuthors.map {
                val author = authorRepository.getByUsername(it.username)
                    ?: TODO("Throw a pretty exception")

                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    privileges = CompositionsGenericPrivileges(modify = it.modify, view = it.view),
                    authorId = author.id,
                    privilegeId = privilegeSourceId
                )
            }

            return carouselOfImagesRepository.compose(
                CarouselOfImagesComposePrepared(
                    name = createRequest.name,
                    imageCollectionId = imageCollectionId,
                    redirectTextCollectionId = redirectsCollectionId,
                    privilegeId = privilegeSourceId,
                )
            )
        }
    }
    // endregion Insert


    // region Delete

    // endregion Delete


    // region CarouselOfImages

    // todo - if multiple comps in one repository then which one to delete?
    // CarouselOfImagesRepository only for sql queries concerning all carousel components
    // Everything else will be extracted into managers. A manager for each component of category (e.g. carousel.CarouselBasicImages)
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