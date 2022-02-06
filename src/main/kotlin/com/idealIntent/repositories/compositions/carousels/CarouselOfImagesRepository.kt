package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionRepositoryStructure
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf

data class CarouselOfImagesComposePrepared(
    val imageCollectionId: Int,
    val redirectTextCollectionId: Int,
    val privilegeId: Int,
    val name: String,
)

/**
 * Carousel of images repository - responsible for carousel_of_images CRUD actions.
 *
 * Check out [getSingleCompositionOfPrivilegedAuthor] to see what the composition consists of.
 */
class CarouselOfImagesRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    // todo replace Image
) : RepositoryBase(), ICompositionRepositoryStructure<CarouselBasicImagesRes, IImagesCarouselEntity,
        CarouselBasicImagesRes, CarouselOfImagesComposePrepared> {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarouselsModel)

    // region Get
    fun getLayout() {

    }

    /**
     * Get single composition of privileged author. Used for testing purposes.
     */
    override fun getSingleCompositionOfPrivilegedAuthor(
        compositionId: Int,
        authorId: Int
    ): List<CarouselBasicImagesRes> = listOf()

    /**
     * Used for testing purposes.
     */
    override fun getAllCompositionsAssociatedOfAuthor(
        authorId: Int
    ): List<CarouselBasicImagesRes> = listOf()

    /**
     * Get all records by criteria query.
     *
     * @param editable Editable by author compositions.
     * @param deletable Deletable by author compositions.
     * @param compositionId Query by composition's unique id.
     * @param authorId Query all compositions associated to author's id.
     */

    // endregion Get

    // region Insert
    override fun compose(composePrepared: CarouselOfImagesComposePrepared): Int? {
        return database.insertAndGenerateKey(ImagesCarouselsModel) {
            set(it.name, composePrepared.name)
            set(it.imageCollectionId, composePrepared.imageCollectionId)
            set(it.redirectTextCollectionId, composePrepared.redirectTextCollectionId)
            set(it.privilegeId, composePrepared.privilegeId)
        } as Int?
    }
// endregion Insert

    override fun getMetadataOfComposition(id: Int): IImagesCarouselEntity? {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}
