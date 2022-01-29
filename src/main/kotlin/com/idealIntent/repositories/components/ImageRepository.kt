package com.idealIntent.repositories.components

import com.idealIntent.exceptions.ServerErrorException
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.serialized.libOfComps.RecordUpdate
import dtos.libOfComps.genericStructures.images.Image
import dtos.libOfComps.genericStructures.images.ImageCollection
import dtos.libOfComps.genericStructures.images.ImageIdentifiableRecordByCol
import dtos.libOfComps.genericStructures.images.ImagesCOL
import models.genericStructures.IImageCollectionSchema
import models.genericStructures.ImageCollections
import models.genericStructures.Images
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf

/**
 * Responsible for image_collections and images table
 */
class ImageRepository : RepositoryBase() {
    private val Database.imageCollections get() = this.sequenceOf(ImageCollections)
    private val Database.images get() = this.sequenceOf(Images)

    /**
     * Get items by collection id
     *
     * @param collectionId
     * @return items by collection id
     */
    fun getAssortmentById(collectionId: Int): ImageCollection { // todo - check privileges if allowed for any author or whether authorId is privileged
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        var collectionOf = ""
        val images = database.from(imgCol)
            .leftJoin(img, img.collectionId eq imgCol.id)
            .select(imgCol.collectionOf, img.orderRank, img.imageTitle, img.imageUrl)

            .where { imgCol.id eq collectionId }
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

    /**
     * Get only image_collection and not it's associated items
     *
     * @param id id of collection
     * @return image_collection but not associated items
     */
    private fun getImageCollection(id: Int): IImageCollectionSchema? {
        return database.imageCollections.find { it.id eq id }
    }


    /**
     * Insert item under a new collection
     *
     * @param image
     * @param collectionOf name the collection
     * @return collectionId or null if failed
     */
    fun insertNewImage(image: Image, collectionOf: String): Int {
        val collectionId = insertImageCollection(collectionOf)

        insertImage(image, collectionId)

        return collectionId
    }

    /**
     * Batch insert items under a new collection
     *
     * @param images
     * @param collectionOf name the collection
     * @return collectionId or null if failed
     */
    fun batchInsertNewImages(images: List<Image>, collectionOf: String): Int { // todo = check mod privileges
        val collectionId = insertImageCollection(collectionOf)

        val ids = batchInsertImages(images, collectionId)

        return collectionId
    }

    /**
     * Insert item
     *
     * @param image
     * @param collectionId id of collection to associate to
     * @return success or fail in creation
     */
    fun insertImage(image: Image, collectionId: Int) {
        val id = database.insert(Images) {
            set(it.orderRank, image.orderRank)
            set(it.imageTitle, image.imageTitle)
            set(it.imageUrl, image.imageUrl)
            set(it.collectionId, collectionId)
        } as Int? ?: TODO()

        // todo - check that ids are all there
    }

    /**
     * Batch insert items
     *
     * @param images
     * @param collectionId id to identify under what collection to insert
     * @return success or fail in creation
     */
    fun batchInsertImages(images: List<Image>, collectionId: Int) {
        val ids = database.batchInsert(Images) {
            images.map { image ->
                item {
                    set(it.orderRank, image.orderRank)
                    set(it.imageTitle, image.imageTitle)
                    set(it.imageUrl, image.imageUrl)
                    set(it.collectionId, collectionId)
                }
            }
        } as IntArray? ?: TODO()
    }

    /**
     * Insert collection in order to group new items under
     *
     * @param collectionOf name the collection
     * @return collectionId or null if failed
     */
    private fun insertImageCollection(collectionOf: String): Int {
        val id = database.insertAndGenerateKey(ImageCollections) {
            set(it.collectionOf, collectionOf)
        } as Int?
            ?: throw ServerErrorException("failed to create ImageCollection", this::class.java)
        return id
    }


    /**
     * Delete an image of collection
     *
     */
    fun deleteImage(collectionId: Int): Boolean {
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        val imageCollection = database.imageCollections.find { imgCol.id eq collectionId } ?: return false
        val effectedRows = database.images.removeIf { it.collectionId eq imageCollection.id }

        return effectedRows > 0
    }

    /**
     * Delete images of collection
     *
     */
    fun batchDeleteImages(collectionId: Int): Boolean {
        TODO()
    }

    /**
     * Delete all images of collection
     *
     */
    fun deleteAllImagesInCollection(collectionId: Int) {
    }

    /**
     * Delete image_collection and it's images
     *
     */
    fun deleteCollectionOfImages() {

    }


    /**
     * Update item
     *
     * @param collectionId id of collection of images
     * @param record update to
     */
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

    /**
     * Batch update items
     *
     * @param collectionId
     * @param records update to
     */
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
}
