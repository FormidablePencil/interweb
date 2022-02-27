package com.idealIntent.models.compositions.basicCollections.texts

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ID2TextCollectionEntity : Entity<ID2TextCollectionEntity> {
    companion object : Entity.Factory<ID2TextCollectionEntity>()

    val id: Int
}

open class D2TextCollectionModel(alias: String?) : Table<ID2TextCollectionEntity>("d2_text_collections", alias) {
    companion object : D2TextCollectionModel(null)

    override fun aliased(alias: String) = D2TextCollectionModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
}
