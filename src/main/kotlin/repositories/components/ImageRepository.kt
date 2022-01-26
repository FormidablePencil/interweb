package repositories.components

import dtos.libOfComps.genericStructures.IImage
import dtos.libOfComps.genericStructures.Image
import dtos.libOfComps.genericStructures.ImageCollection
import models.genericStructures.ImageCollections
import models.genericStructures.Images
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class ImageRepository : RepositoryBase() {
    private val Database.images get() = this.sequenceOf(Images)
    private val Database.imageCollections get() = this.sequenceOf(ImageCollections)

    fun insertCollectionOfImages(images: List<Image>, collectionOf: String): Int? {
        val imageCollectionId = database.insertAndGenerateKey(ImageCollections) {
            set(it.collectionOf, collectionOf)
        } as Int?

        val idsOfImages = database.batchInsert(Images) {
            images.map { image ->
                item {
                    set(it.orderRank, image.orderRank)
                    set(it.imageTitle, image.imageTitle)
                    set(it.imageUrl, image.imageUrl)
                    set(it.collectionId, imageCollectionId)
                }
            }
        }
        return imageCollectionId
    }

    fun getCollectionOfImagesById(id: Int): ImageCollection {
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        var collectionOf = ""

        val images = database.from(imgCol)
            .leftJoin(img, on = imgCol.id eq img.collectionId)
            .select(img.imageUrl, img.imageTitle, img.orderRank, imgCol.collectionOf)
            .map { row ->
                collectionOf = row[imgCol.collectionOf]!!
                object : IImage {
                    override val imageTitle = row[img.imageTitle]!! // todo - may fail
                    override val imageUrl = row[img.imageUrl]!!
                    override val orderRank = row[img.orderRank]!!
                }
            }
        return ImageCollection(collectionOf, images)
    }
}