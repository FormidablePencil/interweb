package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImageCollection
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.images.ImageToCollection
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.models.compositions.basicCollections.images.IImageToCollectionEntity
import com.idealIntent.models.compositions.basicCollections.images.ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.collectionsGeneric.images.ImagesCOL
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf

/**
 * Responsible for collections of images.
 *
 * Overriding functions that call the inherited functions from [ICollectionStructure] are there for structure.
 * Having them in this way makes for easier unit testing.
 *
 * Responsible for -
 * [image][models.compositions.basicsCollections.images.IImage],
 * [image to collections][models.compositions.basicsCollections.images.IImageToCollection],
 * [image collection][models.compositions.basicsCollections.images.IImageCollection].
 */
class ImageRepository() : RepositoryBase(),
    ICollectionStructure<Image, ImagePK, IImageToCollectionEntity, ImageToCollection, ImageCollection> {
    private val Database.imageCollections get() = this.sequenceOf(ImageCollectionsModel)
    private val Database.imageToCollections get() = this.sequenceOf(ImageToCollectionsModel)
    private val Database.images get() = this.sequenceOf(ImagesModel)

    // region Get
    override fun getSingleRecordOfCollection(recordId: Int, collectionId: Int): ImagePK? =
        getRecordsQuery(recordId, collectionId)?.first()

    override fun getAllRecordsOfCollection(collectionId: Int): List<ImagePK>? =
        getRecordsQuery(null, collectionId)

    override fun getRecordsQuery(recordId: Int?, collectionId: Int): List<ImagePK>? {
        val itemToCol = ImageToCollectionsModel.aliased("imgToCol")
        val item = ImagesModel.aliased("img")

        val records = database.from(itemToCol)
            .leftJoin(item, item.id eq itemToCol.imageId)
            .select(itemToCol.orderRank, itemToCol.imageId, item.url, item.description)
            .whereWithConditions {
                it += (item.id eq itemToCol.imageId) and (itemToCol.collectionId eq collectionId)
                if (recordId != null) it += (item.id eq recordId)
            }
            .map { row ->
                ImagePK(
                    id = row[itemToCol.imageId]!!,
                    orderRank = row[itemToCol.orderRank]!!,
                    description = row[item.description]!!,
                    url = row[item.url]!!
                )
            }

        return records.ifEmpty { null }
    }

    override fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean =
        database.imageToCollections.find { (it.imageId eq recordId) and (it.collectionId eq collectionId) } != null
    // endregion Get


    // region Insert
    override fun batchInsertRecordsToNewCollection(records: List<Image>): Int {
        val collectionId = addRecordCollection()
        batchInsertRecordsToCollection(records, collectionId)
        return collectionId
    }

    override fun batchInsertRecordsToCollection(records: List<Image>, collectionId: Int): Boolean {
        records.forEach {
            if (!insertRecordToCollection(it, collectionId))
                return@batchInsertRecordsToCollection false
        }
        return true
    }

    override fun insertRecordToNewCollection(record: Image): Int {
        val collectionId = addRecordCollection()
        insertRecordToCollection(record, collectionId)
        return collectionId
    }

    override fun insertRecordToCollection(record: Image, collectionId: Int): Boolean {
        val id = database.insertAndGenerateKey(ImagesModel) {
            set(it.description, record.description)
            set(it.url, record.url)
        } as Int
        return associateRecordToCollection(orderRank = record.orderRank, recordId = id, collectionId = collectionId)
    }

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(ImageCollectionsModel) { } as Int

    override fun batchAssociateRecordsToCollection(records: List<ImagePK>, collectionId: Int) {
        database.useTransaction {
            if (records.isEmpty()) throw CompositionException(CompositionCode.EmptyListOfRecordsProvided)
            records.forEach {
                try {
                    associateRecordToCollection(
                        orderRank = it.orderRank,
                        collectionId = collectionId,
                        recordId = it.id
                    )
                } catch (ex: Exception) {
                    throw CompositionException(
                        CompositionCode.FailedToAssociateRecordToCollection, it.orderRank.toString(), ex
                    )
                }
            }
        }
    }

    override fun associateRecordToCollection(orderRank: Int, recordId: Int, collectionId: Int): Boolean =
        database.insert(ImageToCollectionsModel) {
            set(it.collectionId, collectionId)
            set(it.imageId, recordId)
            set(it.orderRank, orderRank)
        } == 1
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
