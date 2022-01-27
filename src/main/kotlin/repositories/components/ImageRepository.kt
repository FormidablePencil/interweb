package repositories.components

import dtos.libOfComps.genericStructures.Image
import dtos.libOfComps.genericStructures.ImageCollection
import models.genericStructures.ImageCollections
import models.genericStructures.Images
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class ImageRepository : RepositoryBase() {
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

    fun getCollectionById(id: Int): ImageCollection {
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        var collectionOf = ""
        val images = database.from(imgCol)
            .leftJoin(img, img.collectionId eq imgCol.id)
            .select(imgCol.collectionOf, img.orderRank, img.imageTitle, img.imageUrl)
            .where { imgCol.id eq id }
            .map { row ->
                collectionOf = row[imgCol.collectionOf]!!
                Image(
                    orderRank = row[img.orderRank]!!,
                    imageTitle = row[img.imageTitle]!!,
                    imageUrl = row[img.imageUrl]!!
                )
            }
        return ImageCollection(collectionOf, images)
    }
}