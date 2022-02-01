package com.idealIntent.models.compositions.basicCollections.images

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ID2ImageCollectionEntity : Entity<ID2ImageCollectionEntity> {
    companion object : Entity.Factory<ID2ImageCollectionEntity>()

    val id: Int
}

object D2ImageCollectionsModel : Table<ID2ImageCollectionEntity>("d2_image_collections") {
    val id = int("id").primaryKey().bindTo { it.id }
}
