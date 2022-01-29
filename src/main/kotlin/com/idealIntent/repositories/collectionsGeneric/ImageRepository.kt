package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.compositions.RecordUpdate
import com.idealIntent.exceptions.ServerErrorException
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.compositions.genericStructures.images.Image
import dtos.compositions.genericStructures.images.ImageCollection
import dtos.compositions.genericStructures.images.ImageIdentifiableRecordByCol
import dtos.compositions.genericStructures.images.ImagesCOL
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
class ImageRepository : RepositoryBase(),
    ICollectionStructure<Image, IImageCollectionSchema, ImageCollection> {
    private val Database.imageCollections get() = this.sequenceOf(ImageCollections)
    private val Database.images get() = this.sequenceOf(Images)

    // region Get
    override fun getAssortmentById(id: Int): ImageCollection { // todo - check privileges if allowed for any author or whether authorId is privileged
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        var label = ""
        val images = database.from(imgCol)
            .leftJoin(img, img.collectionId eq imgCol.id)
            .select(imgCol.collectionOf, img.orderRank, img.imageTitle, img.imageUrl)

            .where { imgCol.id eq id }
            .map { row ->
                label = row[imgCol.collectionOf]!!
                Image(
                    orderRank = row[img.orderRank]!!,
                    imageTitle = row[img.imageTitle]!!,
                    imageUrl = row[img.imageUrl]!!
                )
            }
        return ImageCollection(label, images)
    }

    override fun getMetadataOfCollection(id: Int): IImageCollectionSchema? {
        return database.imageCollections.find { it.id eq id }
    }
    // endregion Get


    // region Insert
    override fun insertNewRecord(record: Image, label: String): Int {
        val collectionId = insertRecordCollection(label)

        insertRecord(record, collectionId)

        return collectionId
    }

    override fun batchInsertNewRecords(records: List<Image>, label: String): Int { // todo = check mod privileges
        val collectionId = insertRecordCollection(label)

        val ids = batchInsertRecords(records, collectionId)

        return collectionId
    }

    override fun insertRecord(record: Image, id: Int): Boolean {
        return database.insert(Images) {
            set(it.orderRank, record.orderRank)
            set(it.imageTitle, record.imageTitle)
            set(it.imageUrl, record.imageUrl)
            set(it.collectionId, id)
        } != 0
    }

    override fun batchInsertRecords(records: List<Image>, id: Int): Boolean {
        val ids = database.batchInsert(Images) {
            records.map { image ->
                item {
                    set(it.orderRank, image.orderRank)
                    set(it.imageTitle, image.imageTitle)
                    set(it.imageUrl, image.imageUrl)
                    set(it.collectionId, id)
                }
            }
        } as IntArray? ?: TODO()

        TODO()
    }

    override fun insertRecordCollection(label: String): Int {
        val id = database.insertAndGenerateKey(ImageCollections) {
            set(it.collectionOf, label)
        } as Int?
            ?: throw ServerErrorException("failed to create ImageCollection", this::class.java)
        return id
    }
    // endregion Insert


    // region Update
    override fun updateRecord(record: RecordUpdate, id: Int): Boolean {
        val collection = getMetadataOfCollection(id) ?: return false // todo handle gracefully

        val res = database.update(Images) {
            record.updateTo.map { updateCol ->
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

        TODO()
    }

    override fun batchUpdateRecords(records: List<RecordUpdate>, id: Int): Boolean {
        val collection = getMetadataOfCollection(id) ?: return false // handle gracefully

        database.batchUpdate(Images) {
            records.map { record ->
                item {
                    record.updateTo.map { updateCol ->
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
        TODO()
    }
    // endregion Update


    // region Delete
    override fun deleteRecord(id: Int): Boolean {
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        val imageCollection = database.imageCollections.find { imgCol.id eq id } ?: return false
        val effectedRows = database.images.removeIf { it.collectionId eq imageCollection.id }

        return effectedRows > 0
    }

    override fun batchDeleteRecords(id: Int): Boolean {
        TODO()
    }

    override fun deleteAllRecordsInCollection(id: Int) {
    }

    override fun deleteCollectionOfRecords() {

    }
    // endregion Delete
}
