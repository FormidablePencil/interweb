package repositories.components

import dtos.libOfComps.carousels.CarouselBasicImages
import dtos.libOfComps.genericStructures.Image
import dtos.libOfComps.genericStructures.PrivilegedAuthor
import dtos.libOfComps.genericStructures.Text
import models.libOfComps.carousels.ImagesCarousels
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class CarouselRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val privilegeRepository: PrivilegeRepository
) : RepositoryBase() {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarousels)

    // region CarouselOfImages
    fun insertCarouselBasicImages(component: CarouselBasicImages): Int? {

        // region todo - could be in a caroutine
        val imageCollectionId = imageRepository.insertCollectionOfImages(
            component.images, "CarouselBasicImages component"
        )
        val navToTextCollectionId = textRepository.insertCollectionOfTexts(
            component.navToCorrespondingImagesOrder, "carouselNavLinks"
        )
        val privilegeId = privilegeRepository.insertPrivileges(
            component.privilegedAuthors, "carousel of images"
        )
        // endregion

        // todo - validate ids, println(ids)

        // todo - could be in a separate caroutine than the first in this scope
        return database.insertAndGenerateKey(ImagesCarousels) {
            set(it.imageCollectionId, imageCollectionId)
            set(it.navToTextCollectionId, navToTextCollectionId)
            set(it.privilegeId, privilegeId)
            set(it.title, component.title)
        } as Int?
        // endregion
    }

    fun getCarouselBasicImagesById(id: Int): CarouselBasicImages {
        var title = ""
        var images = listOf<Image>()
        var navTos = listOf<Text>()
        var privilegedAuthors = listOf<PrivilegedAuthor>()

        val crslImg = ImagesCarousels.aliased("crlsImg")

        for (row in database.from(crslImg)
            .select(crslImg.imageCollectionId, crslImg.navToTextCollectionId, crslImg.privilegeId, crslImg.title)
            .where { crslImg.id eq id }) {
            title = row[crslImg.title]!!
            images = imageRepository.getAssortmentById(row[crslImg.imageCollectionId]!!).images
            navTos = textRepository.getAssortmentById(row[crslImg.navToTextCollectionId]!!).texts
            privilegedAuthors = privilegeRepository.getAssortmentById(row[crslImg.privilegeId]!!).privilegedAuthors
        }

        return CarouselBasicImages(
            title = title, // todo - may not work
            images = images,
            navToCorrespondingImagesOrder = navTos,
            privilegedAuthors = privilegedAuthors
        )
    }

    fun deleteCarouselOfImagesById(id: Int): Boolean {
        return database.imagesCarousels.removeIf { it.id eq id } != 0
    }

    // region update

    fun batchUpdate(componentId: Int, table: CarouselOfImagesTABLE, updateToData: List<RecordUpdate>) {
        when (table) {
            CarouselOfImagesTABLE.Images ->
                imageRepository.batchUpdateImages(componentId, updateToData)
            CarouselOfImagesTABLE.NavTos ->
                textRepository.batchUpdateTexts(componentId, updateToData)
            CarouselOfImagesTABLE.Privileges ->
                privilegeRepository.batchUpdatePrivilegedAuthors(componentId, updateToData)
        }
    }

    fun update(componentId: Int, column: CarouselOfImagesTABLE, updateToData: RecordUpdate) {
        when (column) {
            CarouselOfImagesTABLE.Images ->
                imageRepository.updateImage(componentId, updateToData)
            CarouselOfImagesTABLE.NavTos ->
                textRepository.updateText(componentId, updateToData)
            CarouselOfImagesTABLE.Privileges ->
                privilegeRepository.updatePrivilege(componentId, updateToData)
        }
    }
    // endregion update
    // endregion
}
