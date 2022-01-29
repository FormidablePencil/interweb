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
class ImageRepository : RepositoryBase(), ICompRecordCrudStructure<Image, IImageCollectionSchema, ImageCollection> {
    private val Database.imageCollections get() = this.sequenceOf(ImageCollections)
    private val Database.images get() = this.sequenceOf(Images)

    // region Get
    override fun getAssortmentById(collectionId: Int): ImageCollection { // todo - check privileges if allowed for any author or whether authorId is privileged
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

    override fun getCollection(id: Int): IImageCollectionSchema? {
        return database.imageCollections.find { it.id eq id }
    }
    // endregion Get


    // region Insert
    override fun insertNewRecord(record: Image, collectionOf: String): Int {
        val collectionId = insertRecordCollection(collectionOf)

        insertRecord(record, collectionId)

        return collectionId
    }

    override fun batchInsertNewRecords(records: List<Image>, collectionOf: String): Int { // todo = check mod privileges
        val collectionId = insertRecordCollection(collectionOf)

        val ids = batchInsertRecords(records, collectionId)

        return collectionId
    }

    override fun insertRecord(record: Image, collectionId: Int): Boolean {
        val id = database.insert(Images) {
            set(it.orderRank, record.orderRank)
            set(it.imageTitle, record.imageTitle)
            set(it.imageUrl, record.imageUrl)
            set(it.collectionId, collectionId)
        } as Int? ?: TODO()

        // todo - check that ids are all there
    }

    override fun batchInsertRecords(records: List<Image>, collectionId: Int): Boolean {
        val ids = database.batchInsert(Images) {
            records.map { image ->
                item {
                    set(it.orderRank, image.orderRank)
                    set(it.imageTitle, image.imageTitle)
                    set(it.imageUrl, image.imageUrl)
                    set(it.collectionId, collectionId)
                }
            }
        } as IntArray? ?: TODO()
    }

    override fun insertRecordCollection(collectionOf: String): Int {
        val id = database.insertAndGenerateKey(ImageCollections) {
            set(it.collectionOf, collectionOf)
        } as Int?
            ?: throw ServerErrorException("failed to create ImageCollection", this::class.java)
        return id
    }
    // endregion Insert


    // region Update
    override fun updateRecord(collectionId: Int, record: RecordUpdate): Boolean {
        val collection = getCollection(collectionId) ?: return false // todo handle gracefully

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
    }

    override fun batchUpdateRecords(collectionId: Int, records: List<RecordUpdate>): Boolean {
        val collection = getCollection(collectionId) ?: return // handle gracefully

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
    }
    // endregion Update


    // region Delete
    override fun deleteRecord(collectionId: Int): Boolean {
        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        val imageCollection = database.imageCollections.find { imgCol.id eq collectionId } ?: return false
        val effectedRows = database.images.removeIf { it.collectionId eq imageCollection.id }

        return effectedRows > 0
    }

    override fun batchDeleteRecords(collectionId: Int): Boolean {
        TODO()
    }

    override fun deleteAllRecordsInCollection(collectionId: Int) {
    }

    override fun deleteCollectionOfRecords() {

    }
    // endregion Delete
}
