package com.idealIntent.models.compositions.basicCollections.images

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IImageCollectionToD2CollectionEntity : Entity<IImageCollectionToD2CollectionEntity>, IWithOrder, ImageCollectionToD2Collection{
    companion object : Entity.Factory<IImageCollectionToD2CollectionEntity>()
}

open class ImageCollectionToD2CollectionsModel(alias: String?) :
    Table<IImageCollectionToD2CollectionEntity>("d2_image_collection_items", alias) {
    companion object : ImageCollectionToD2CollectionsModel(null)

    override fun aliased(alias: String) = ImageCollectionToD2CollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val d2ImageCollectionId = int("d2_image_collection_id").references(D2ImageCollectionsModel) { it.d2ImageCollection }
    val imageCollectionId = int("image_collection_id").references(ImageCollectionsModel) { it.imageCollection }
}
