package com.idealIntent.models.compositions.basicCollections.texts

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ITextToCollectionEntity : Entity<ITextToCollectionEntity>, IWithOrder {
    companion object : Entity.Factory<ITextToCollectionEntity>()

    val metadata: ITextCollectionEntity
    val texts: ITextEntity
}

open class TextToCollectionsModel(alias: String?) :
    Table<ITextToCollectionEntity>("text_to_collections", alias) {
    companion object : TextToCollectionsModel(null)

    override fun aliased(alias: String) = TextToCollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val collectionId = int("collection_id").references(TextCollectionsModel) { it.metadata }
    val textId = int("text_id").references(TextsModel) { it.texts }
}
