package repositories.components

import dtos.libOfComps.genericStructures.Image
import dtos.libOfComps.genericStructures.ImageCollection
import kotlinx.serialization.Serializable
import models.genericStructures.IImageCollectionSchema
import models.genericStructures.ImageCollections
import models.genericStructures.Images
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class ImageRepository : RepositoryBase() {
    private val Database.imageCollections get() = this.sequenceOf(ImageCollections)
    private val Database.images get() = this.sequenceOf(Images)

    fun insertCollectionOfImages(images: List<Image>, collectionOf: String): Int? { // todo = check mod privileges
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

    fun getAssortmentById(imageCollectionId: Int): ImageCollection { // todo - check privileges if allowed for any author or whether authorId is privileged
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        var collectionOf = ""
        val images = database.from(imgCol)
            .leftJoin(img, img.collectionId eq imgCol.id)
            .select(imgCol.collectionOf, img.orderRank, img.imageTitle, img.imageUrl)

            .where { imgCol.id eq imageCollectionId }
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

    fun deleteImage(collectionId: Int): Boolean {
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        val imageCollection = database.imageCollections.find { imgCol.id eq collectionId } ?: return false
        val effectedRows = database.images.removeIf { it.collectionId eq imageCollection.id }

        return effectedRows > 0
    }

    fun batchUpdateImages(collectionId: Int, records: List<RecordUpdate>) {
        val collection = getImageCollection(collectionId) ?: return // handle gracefully

        database.batchUpdate(Images) {
            records.map { record ->
                item {
                    record.updateRecord.map { updateCol ->
                        when (ImagesCOL.fromInt(updateCol.column)) {
                            ImagesCOL.ImageUrl -> set(it.imageUrl, updateCol.value)
                            ImagesCOL.ImageTitle -> set(it.imageTitle, updateCol.value)
                            ImagesCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
                            // todo - toInt() may fail
                        }
                        where {
                            when (ImageIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                                ImageIdentifiableRecordByCol.OrderRank ->
                                    (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
                            } // todo - handle incorrect recordIdentifiableByCol gracefully
                        }
                    }
                }
            }
        }
    }

    fun updateImage(collectionId: Int, record: RecordUpdate) {
        val collection = getImageCollection(collectionId) ?: return // handle gracefully

        val res = database.update(Images) {
            record.updateRecord.map { updateCol ->
                when (ImagesCOL.fromInt(updateCol.column)) {
                    ImagesCOL.ImageUrl -> set(it.imageUrl, updateCol.value)
                    ImagesCOL.ImageTitle -> set(it.imageTitle, updateCol.value)
                    ImagesCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
                    // todo - toInt() may fail
                }
            }
            where {
                when (ImageIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                    ImageIdentifiableRecordByCol.OrderRank ->
                        (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
                } // todo - handle incorrect recordIdentifiableByCol gracefully
            }
        }
    }

    private fun getImageCollection(id: Int): IImageCollectionSchema? {
        return database.imageCollections.find { it.id eq id }
    }
}

@Serializable
data class UpdateColumn(override val column: Int, override val value: String) : IUpdateColumn

interface IUpdateColumn {
    val column: Int
    val value: String
}

@Serializable
data class RecordUpdate(
    override val recordIdentifiableByCol: Int, // maps to enums
    override val recordIdentifiableByColOfValue: String,
    override val updateRecord: List<UpdateColumn>,
): IRecordUpdate

interface IRecordUpdate {
    val recordIdentifiableByCol: Int // maps to enums
    val recordIdentifiableByColOfValue: String
    val updateRecord: List<UpdateColumn>
}

enum class CarouselOfImagesTABLE(private val value: Int) {
    Images(0), NavTos(1), Privileges(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

enum class ImagesCOL(private val value: Int) {
    ImageUrl(0), ImageTitle(1), OrderRank(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

enum class ImageIdentifiableRecordByCol(private val value: Int) {
    OrderRank(0);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}