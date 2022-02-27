package com.idealIntent.models.compositions.basicCollections.texts

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface TextCollectionToD2Collection {
    val d2ImageCollectionId: Int
    val imageCollectionId: Int
}

interface ITextCollectionToD2CollectionEntity : Entity<ITextCollectionToD2CollectionEntity>, IWithOrder, TextCollectionToD2Collection{
    companion object : Entity.Factory<ITextCollectionToD2CollectionEntity>()
}

open class TextCollectionToD2CollectionsModel(alias: String?) :
    Table<ITextCollectionToD2CollectionEntity>("d2_text_collection_items", alias) {
    companion object : TextCollectionToD2CollectionsModel(null)

    override fun aliased(alias: String) = TextCollectionToD2CollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val d2ImageCollectionId = int("d2_collection_id").bindTo { it.d2ImageCollectionId }
    val imageCollectionId = int("collection_id").bindTo { it.imageCollectionId }
}
