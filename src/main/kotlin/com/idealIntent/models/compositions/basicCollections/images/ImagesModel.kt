package com.idealIntent.models.compositions.basicCollections.images

import models.compositions.basicsCollections.images.IImage
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface IImageEntity : Entity<IImageEntity>, IImage {
    companion object : Entity.Factory<IImageEntity>()

    val imageCollection: IImageCollectionEntity
}

open class ImagesModel(alias: String?) : Table<IImageEntity>("images", alias) {
    companion object : ImagesModel(null)

    override fun aliased(alias: String) = ImagesModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    var description = varchar("description").bindTo { it.description }
    var url = varchar("url").bindTo { it.url }
}