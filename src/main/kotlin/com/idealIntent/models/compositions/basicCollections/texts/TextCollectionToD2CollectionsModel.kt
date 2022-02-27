package com.idealIntent.models.compositions.basicCollections.texts

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ITextCollectionToD2CollectionEntity : Entity<ITextCollectionToD2CollectionEntity>, IWithOrder,
    IRecordCollectionToD2Collection {
    companion object : Entity.Factory<ITextCollectionToD2CollectionEntity>()
}

open class TextCollectionToD2CollectionsModel(alias: String?) :
    Table<ITextCollectionToD2CollectionEntity>("text_collection_to_d2_collections", alias) {
    companion object : TextCollectionToD2CollectionsModel(null)

    override fun aliased(alias: String) = TextCollectionToD2CollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val d2CollectionId = int("d2_collection_id").bindTo { it.d2CollectionId }
    val collectionId = int("collection_id").bindTo { it.collectionId }
}
