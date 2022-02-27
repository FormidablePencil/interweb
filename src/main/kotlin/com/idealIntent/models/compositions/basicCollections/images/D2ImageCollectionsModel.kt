package com.idealIntent.models.compositions.basicCollections.images

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ID2ImageCollectionEntity : Entity<ID2ImageCollectionEntity> {
    companion object : Entity.Factory<ID2ImageCollectionEntity>()

    val id: Int
}

open class D2ImageCollectionsModel(alias: String?) : Table<ID2ImageCollectionEntity>("d2_image_collections", alias) {
    companion object : D2ImageCollectionsModel(null)

    override fun aliased(alias: String) = D2ImageCollectionsModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
}
