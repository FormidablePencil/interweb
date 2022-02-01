package com.idealIntent.models.compositions.basicCollections.images

import models.compositions.basicsCollections.images.IImageCollection
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IImageCollectionEntity : Entity<IImageCollectionEntity>, IImageCollection {
    companion object : Entity.Factory<IImageCollectionEntity>()
}

open class ImageCollectionsModel(alias: String?) :
    Table<IImageCollectionEntity>("image_collections", alias) {
    companion object : ImageCollectionsModel(null)

    override fun aliased(alias: String) = ImageCollectionsModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
}
