package com.idealIntent.models.compositions.basicCollections.images

import com.idealIntent.models.compositions.basicCollections.texts.IRecordCollectionToD2Collection
import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IImageCollectionToD2CollectionEntity : Entity<IImageCollectionToD2CollectionEntity>, IWithOrder,
    IRecordCollectionToD2Collection {
    companion object : Entity.Factory<IImageCollectionToD2CollectionEntity>()
}

open class ImageCollectionToD2CollectionsModel(alias: String?) :
    Table<IImageCollectionToD2CollectionEntity>("image_collection_to_d2_collections", alias) {
    companion object : ImageCollectionToD2CollectionsModel(null)

    override fun aliased(alias: String) = ImageCollectionToD2CollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val d2CollectionId = int("d2_collection_id").bindTo { it.d2CollectionId }
    val collectionId = int("collection_id").bindTo { it.collectionId }
}
