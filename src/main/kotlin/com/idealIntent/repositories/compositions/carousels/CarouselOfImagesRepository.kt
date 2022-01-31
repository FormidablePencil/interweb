package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.RecordUpdate
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionStructure
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.carousels.CarouselOfImagesTABLE
import dtos.compositions.genericStructures.images.Image
import dtos.compositions.genericStructures.privileges.PrivilegedAuthor
import dtos.compositions.genericStructures.texts.Text
import models.composition.carousels.IImagesCarouselEntity
import models.composition.carousels.ImagesCarousels
import models.compositions.carousels.ImagesCarousels
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf

// todo - get ICollectionStructure implemented or create a new one just for Composition lvl repositories
class CarouselOfImagesRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val privilegeRepository: PrivilegeRepository
    // todo replace Image
) : RepositoryBase(), ICompositionStructure<IImagesCarouselEntity, CarouselBasicImages> {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarousels)
    // todo - there will be multiple kinds of carousels thus will be a component of a component some day

    // region Get
    override fun getComposition(id: Int): CarouselBasicImages {
        var title = ""
        var images = listOf<Image>()
        var navTos = listOf<Text>()
        var privilegedAuthors = listOf<PrivilegedAuthor>()

        val crslImg = ImagesCarousels.aliased("crlsImg")

        for (row in database.from(crslImg)
            .select(crslImg.imageCollectionId, crslImg.navToTextCollectionId, crslImg.privilegeId, crslImg.title)
            .where { crslImg.id eq id }) {
            title = row[crslImg.title]!!
            images = imageRepository.getCollectionOfRecords(row[crslImg.imageCollectionId]!!).images
            navTos = textRepository.getCollectionOfRecords(row[crslImg.navToTextCollectionId]!!).texts
            privilegedAuthors = privilegeRepository.getCollectionOfRecords(row[crslImg.privilegeId]!!).privilegedAuthors
        }

        return CarouselBasicImages(
            title = title, // todo - may not work
            images = images,
            navToCorrespondingImagesOrder = navTos,
            privilegedAuthors = privilegedAuthors
        )
    }

    override fun getMetadataOfComposition(id: Int): IImagesCarouselEntity {
        TODO()
    }
    // endregion Get


    // region Insert
    override fun insertComposition(composition: CarouselBasicImages): Int? {
        // region todo - could be in a caroutine
        val imageCollectionId = imageRepository.batchInsertNewRecords(
            composition.images
        )
        val navToTextCollectionId = textRepository.batchInsertNewRecords(
            composition.navToCorrespondingImagesOrder
        )
        val privilegeId = privilegeRepository.batchInsertNewRecords(
            composition.privilegedAuthors
        )
        // endregion

        // todo - validate ids, println(ids)

        // todo - could be in a separate caroutine than the first in scope
        return database.insertAndGenerateKey(ImagesCarousels) {
            set(it.imageCollectionId, imageCollectionId)
            set(it.navToTextCollectionId, navToTextCollectionId)
            set(it.privilegeId, privilegeId)
            set(it.title, composition.title)
        } as Int?
        // endregion
    }

    // endregion Insert


    // region Delete

    // endregion Delete


    // region CarouselOfImages

    // todo - if multiple comps in one repository then which one to delete?
    // CarouselOfImagesRepository only for sql queries concerning all carousel components
    // Everything else will be extracted into managers. A manager for each component of category (e.g. carousel.CarouselBasicImages)
    override fun deleteComposition(id: Int): Boolean {
        return database.imagesCarousels.removeIf { it.id eq id } != 0
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
                privilegeRepository.batchUpdateRecords(updateToData, componentId)
        }
    }

    fun update(componentId: Int, column: CarouselOfImagesTABLE, updateToData: RecordUpdate) {
        when (column) {
            CarouselOfImagesTABLE.Images ->
                imageRepository.updateRecord(updateToData,, componentId)
            CarouselOfImagesTABLE.NavTos ->
                textRepository.updateRecord(updateToData,, componentId)
            CarouselOfImagesTABLE.Privileges ->
                privilegeRepository.updateRecord(updateToData,, componentId)
        }
    }


    override fun insertCompositions(components: List<CarouselBasicImages>, label: String): Int? {
        TODO("Not yet implemented")
    }

    override fun updateComposition(id: Int, record: RecordUpdate): Boolean {
        TODO("Not yet implemented")
    }

    override fun batchUpdateCompositions(id: Int, records: List<RecordUpdate>): Boolean {
        TODO("Not yet implemented")
    }

    override fun batchDeleteCompositions(id: Int): Boolean {
        TODO("Not yet implemented")
    }
    // endregion
}
