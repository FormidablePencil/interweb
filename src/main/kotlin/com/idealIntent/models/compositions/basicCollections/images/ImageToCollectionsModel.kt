package com.idealIntent.models.compositions.basicCollections.images

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

/**
 * Image and collection records queried by ids of [imageUrl to collection][IImageToCollection].
 */
interface IImageToCollectionEntity : Entity<IImageToCollectionEntity>, IWithOrder {
    companion object : Entity.Factory<IImageToCollectionEntity>()

    val collectionMetadata: IImageCollectionEntity
    val image: IImageEntity
}

open class ImageToCollectionsModel(alias: String?) :
    Table<IImageToCollectionEntity>("image_to_collections", alias) {
    companion object : ImageToCollectionsModel(null)

    override fun aliased(alias: String) = ImageToCollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val collectionId = int("collection_id").references(ImageCollectionsModel) { it.collectionMetadata }
    val imageId = int("image_id").references(ImagesModel) { it.image }
}
