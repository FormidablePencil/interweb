package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImageCollection
import com.idealIntent.dtos.collectionsGeneric.images.ImageToCollection
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.ServerErrorException
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.collectionsGeneric.images.ImagesCOL
import models.compositions.basicsCollections.images.IImageToCollectionEntity
import models.compositions.basicsCollections.images.ImageCollectionsModel
import models.compositions.basicsCollections.images.ImageToCollectionsModel
import models.compositions.basicsCollections.images.ImagesModel
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf

/**
 * Responsible for image_collections and images table
 */
class ImageRepository : RepositoryBase(),
    ICollectionStructure<Image, IImageToCollectionEntity, ImageToCollection, ImageCollection> {
    private val Database.imageCollections get() = this.sequenceOf(ImageCollectionsModel)
    private val Database.imageToCollections get() = this.sequenceOf(ImageToCollectionsModel)
    private val Database.images get() = this.sequenceOf(ImagesModel)

    // region Get
    override fun getCollectionOfRecords(recordId: Int, collectionId: Int): ImageCollection {
        // todo - check privileges if allowed for any author or whether authorId is privileged
        val imgCol = ImageCollectionsModel.aliased("imgCol")
        val imgToCol = ImageToCollectionsModel.aliased("imgToCol")
        val img = ImagesModel.aliased("img")

        val images = database.from(imgToCol) // should automatically join
////            .leftJoin(img, img.collectionId eq imgToCol.id)
////            .leftJoin(img, img.collectionId eq imgToCol.id)
            .select(imgCol.id, imgToCol.orderRank, img.url)
            .where { (img.id eq imgToCol.imageId) and (imgCol.id eq imgToCol.collectionId) }
            .map { row ->
                Image(
                    id = row[imgToCol.imageId]!!,
                    orderRank = row[imgToCol.orderRank]!!,
                    description = row[img.description]!!,
                    url = row[img.url]!!
                )
            }

        return ImageCollection(collectionId, images)
    }

    override fun getRecordsToCollectionInfo(recordId: Int, collectionId: Int): IImageToCollectionEntity? =
        database.imageToCollections.find { (it.imageId eq recordId) and (it.collectionId eq collectionId) }

    // todo - todo if there are multiple records under the ids
    // endregion Get


    // region Insert
    override fun insertNewRecord(record: Image): ImageToCollection? =
        insertRecord(record, addRecordCollection())

    // todo = check mod privileges (maybe at manager level)
    override fun batchInsertNewRecords(records: List<Image>): List<ImageToCollection>? =
        batchInsertRecords(records, addRecordCollection())

    override fun insertRecord(record: Image, collectionId: Int): ImageToCollection? {
        val imageId = database.insertAndGenerateKey(ImagesModel) {
            set(it.description, record.description)
            set(it.url, record.url)
        } as Int? ?: return null

        val rowOfColEffected = database.insert(ImageToCollectionsModel) {
            set(it.collectionId, collectionId)
            set(it.imageId, imageId)
            set(it.orderRank, collectionId)
        } as Int != 0
        return if (rowOfColEffected) null  // todo - and revert image insertion
        else ImageToCollection(orderRank = record.orderRank, collectionId = collectionId, imageId = imageId)
    }

    override fun batchInsertRecords(records: List<Image>, collectionId: Int): List<ImageToCollection>? =
        records.map {
            return@map insertRecord(it, collectionId)
                ?: TODO("throw exception of one fails. Either one inserts or non do.")
        }

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(ImageCollectionsModel) {} as Int?
            ?: throw ServerErrorException("failed to create ImageCollection (should always succeed)", this::class.java)
    // endregion Insert


    // region Update
    // todo = check mod privileges (maybe at manager level)
    override fun updateRecord(record: RecordUpdate, imageId: Int, collectionId: Int): Boolean {
        val collection =
            validateRecordToCollectionRelationship(imageId, collectionId) ?: return false // todo handle gracefully

        val res = database.update(ImagesModel) {
            record.updateTo.map { updateCol ->
                when (ImagesCOL.fromInt(updateCol.column)) {
                    ImagesCOL.Url -> set(it.url, updateCol.value)
                    ImagesCOL.Description -> set(it.description, updateCol.value)
//                    ImagesCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
                    // todo - toInt() may fail
                }
            }
            where {
                TODO()
//                (img.id eq imgToCol.imageId) and (imgCol.id eq imgToCol.collectionId)
//                when (ImageIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
//                    ImageIdentifiableRecordByCol.OrderRank ->
//                        (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
//                } // todo - handle incorrect recordIdentifiableByCol gracefully
            }
        }

        TODO()
    }

    override fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean {
        database.batchUpdate(ImagesModel) {
            records.map { record ->
//                val collection = validateRecordToCollectionRelationship(record.recordId, collectionId) ?: return false // handle gracefully
                item {
                    record.updateTo.map { updateCol ->
                        when (ImagesCOL.fromInt(updateCol.column)) {
                            ImagesCOL.Url -> set(it.url, updateCol.value)
                            ImagesCOL.Description -> set(it.description, updateCol.value)
//                            ImagesCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
                            // todo - toInt() may fail
                        }
                        where {
                            TODO()
//                            when (ImageIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
//                                ImageIdentifiableRecordByCol.OrderRank ->
//                                    (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
//                            } // todo - handle incorrect recordIdentifiableByCol gracefully
                        }
                    }
                }
            }
        }
        TODO()
    }
    // endregion Update


    // region Delete
    // todo - a message would be more appropriate to return
    override fun deleteRecord(recordId: Int, collectionId: Int): Boolean =
        if (!validateRecordToCollectionRelationship(recordId, collectionId)) false
        else database.images.removeIf { it.id eq collectionId } == 0

    override fun batchDeleteRecords(id: Int, collectionId: Int): Boolean {
        TODO()
    }

    override fun deleteAllRecordsInCollection(collectionId: Int) {
    }

    override fun disassociateRecordFromCollection(recordId: Int, collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionButNotRecord() {

    }
    // endregion Delete
}
