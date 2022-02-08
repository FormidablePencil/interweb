package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextCollection
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.collectionsGeneric.texts.TextToCollection
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.ITextToCollectionEntity
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import dtos.collectionsGeneric.texts.TextsCOL
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.text

/**
 * Responsible for collections of texts.
 *
 * Overriding functions that call the inherited functions from [ICollectionStructure] are there for structure.
 * Having them in this way makes for easier unit testing.
 *
 * Responsible for -
 * [text][models.compositions.basicsCollections.texts.IText],
 * [text to collections][models.compositions.basicsCollections.texts.ITextToCollection],
 * [text collection][models.compositions.basicsCollections.texts.ITextCollection].
 */
class TextRepository : RepositoryBase(),
    ICollectionStructure<Text, TextPK, ITextToCollectionEntity, TextToCollection, TextCollection> {
    private val Database.textCollections get() = this.sequenceOf(TextCollectionsModel)
    private val Database.textToCollections get() = this.sequenceOf(TextToCollectionsModel)
    private val Database.texts get() = this.sequenceOf(TextsModel)

    val text2Col = TextToCollectionsModel.aliased("textRedirect2Col")
    val text = TextsModel.aliased("textRedirect")

    // region Get
    override fun getSingleRecordOfCollection(recordId: Int, collectionId: Int): TextPK? =
        getRecordsQuery(recordId, collectionId)?.first()

    override fun getAllRecordsOfCollection(collectionId: Int): List<TextPK>? =
        getRecordsQuery(null, collectionId)

    override fun getRecordsQuery(recordId: Int?, collectionId: Int): List<TextPK>? {
        val itemToCol = TextToCollectionsModel.aliased("textToCol")
        val item = TextsModel.aliased("text")

        val records = database.from(itemToCol)
            .leftJoin(item, item.id eq itemToCol.textId)
            .select(itemToCol.textId, itemToCol.orderRank, item.text)
            .whereWithConditions {
                it += (item.id eq itemToCol.textId) and (itemToCol.collectionId eq collectionId)
                if (recordId != null) it += (item.id eq recordId)
            }
            .map { row ->
                TextPK(
                    id = row[itemToCol.textId]!!,
                    orderRank = row[itemToCol.orderRank]!!,
                    text = row[item.text]!!,
                )
            }

        return records.ifEmpty { null }
    }

    override fun validateRecordToCollectionRelationship(recordId: Int, collectionId: Int): Boolean =
        database.textToCollections.find { (it.textId eq recordId) and (it.collectionId eq collectionId) } != null
    // endregion Get


    // region Insert
    override fun batchInsertRecordsToNewCollection(records: List<Text>): Int {
        val collectionId = addRecordCollection()
        batchInsertRecordsToCollection(records, collectionId)
        return collectionId
    }

    override fun batchInsertRecordsToCollection(records: List<Text>, collectionId: Int): Boolean {
        records.forEach {
            if (!insertRecordToCollection(it, collectionId))
                return@batchInsertRecordsToCollection false
        }
        return true
    }

    override fun insertRecordToNewCollection(record: Text): Int {
        val collectionId = addRecordCollection()
        insertRecordToCollection(record, collectionId)
        return collectionId
    }

    override fun insertRecordToCollection(record: Text, collectionId: Int): Boolean {
        val id = database.insertAndGenerateKey(TextsModel) {
            set(it.text, record.text)
        } as Int
        return associateRecordToCollection(orderRank = record.orderRank, recordId = id, collectionId = collectionId)
    }

    override fun addRecordCollection(): Int =
        database.insertAndGenerateKey(TextCollectionsModel) { } as Int

    override fun batchAssociateRecordsToCollection(records: List<TextPK>, collectionId: Int) {
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
        database.insert(TextToCollectionsModel) {
            set(it.collectionId, collectionId)
            set(it.textId, recordId)
            set(it.orderRank, orderRank)
        } == 1
    // endregion Insert

    override fun updateRecord(record: RecordUpdate, collectionId: Int) {
        database.useTransaction {
            var doUpdateOrderRank: Boolean = false
            var updateOrderRankTo: String? = null
            val effectedRecords = database.update(TextsModel) {
                record.updateTo.map { updateCol ->
                    when (TextsCOL.fromInt(updateCol.column)) {
                        TextsCOL.Text ->
                            set(it.text, updateCol.value)
                        // todo - orderRank is now of record2Col
                        // todo - toInt() may fail
                        TextsCOL.OrderRank -> {
                            doUpdateOrderRank = true
                            updateOrderRankTo = updateCol.value
                        }
                    }
                }
                where { text.id eq record.recordId }
            }
            if (effectedRecords == 0)
                throw CompositionException(CompositionCode.FailedToAddRecordToCompositionValidator)

            if (doUpdateOrderRank) {
                val updateTo = updateOrderRankTo?.toInt()
                    ?: throw CompositionException(CompositionCode.FailedToAddRecordToCompositionValidator)
                val effectedCollections = database.update(TextToCollectionsModel) {
                    set(it.orderRank, updateTo)
                    where { it.textId eq record.recordId }
                }
                if (effectedCollections == 0)
                    throw CompositionException(CompositionCode.FailedToAddRecordToCompositionValidator)
            }
        }
    }


    // region Update
//    override fun updateRecord(record: RecordUpdate, imageId: Int, collectionId: Int): Boolean {
////        val collection =
////            validateRecordToCollectionRelationship(collectionId) ?: return false // todo - handle gracefully
//
//        database.update(TextsModel) {
//            record.updateTo.map { recordCol ->
//                when (TextsCOL.fromInt(recordCol.column)) {
//                    TextsCOL.Text -> set(it.text, recordCol.value)
////                    TextsCOL.OrderRank -> set(it.orderRank, recordCol.value.toInt()) // todo - may fail
//                    // todo - toInt() may fail
//                }
//            }
//            where {
//                when (TextIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
//                    TextIdentifiableRecordByCol.OrderRank ->
//                        TODO()
////                        (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
//                } // todo - handle incorrect recordIdentifiableByCol gracefully
//                // todo - handle failure gracefully
//            }
//        }
//        TODO("validate if successful")
//    }
//
//    override fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean {
////        val collection = validateRecordToCollectionRelationship(collectionId) ?: return false // todo - hadle gracefully
//
//        database.batchUpdate(TextsModel) {
//            records.map { record ->
//                item {
//                    record.updateTo.map { updateCol ->
//                        when (TextsCOL.fromInt(updateCol.column)) {
//                            TextsCOL.Text -> set(it.text, updateCol.value)
////                            TextsCOL.OrderRank -> set(it.orderRank, updateCol.value.toInt())
//                            // todo - toInt() may fail, handle gracefully
//                        }
//                    }
//                    where {
//                        when (TextIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
//                            TextIdentifiableRecordByCol.OrderRank ->
//                                TODO()
////                                (it.collectionId eq collection.id) and (it.orderRank eq record.recordIdentifiableByColOfValue.toInt())
//                        } // todo - handle incorrect recordIdentifiableByCol gracefully
//                    }
//                }
//            }
//        }
//        TODO("validate if successful")
//    }
// endregion Update


    // region Delete
    override fun deleteRecord(recordId: Int, collectionId: Int): Boolean {

        TODO("Not yet implemented")
    }

    override fun batchDeleteRecords(id: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteAllRecordsInCollection(collectionId: Int) {
        database.useTransaction {
            // get record ids of collection
            val textIds = database.from(TextToCollectionsModel)
                .select(TextToCollectionsModel.textId)
                .where { TextToCollectionsModel.collectionId eq collectionId }
                .map { it[TextToCollectionsModel.textId]!! }

            // delete association between records and collection then collection and records
            database.delete(TextToCollectionsModel) { it.collectionId eq collectionId }
            database.delete(TextCollectionsModel) { it.id eq collectionId }
            textIds.map { database.delete(TextsModel) { it.id eq it.id } }
        }
    }

    override fun disassociateRecordFromCollection(recordId: Int, collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionButNotRecord() {
        TODO("Not yet implemented")
    }

    override fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }
// endregion Delete
}
