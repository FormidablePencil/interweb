package com.idealIntent.models.compositions.basicCollections.texts

import models.compositions.basicsCollections.texts.ITextCollection
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ITextCollectionEntity : Entity<ITextCollectionEntity>, ITextCollection {
    companion object : Entity.Factory<ITextCollectionEntity>()
}

open class TextCollectionsModel(alias: String?) : Table<ITextCollectionEntity>("text_collections", alias) {
    companion object : TextCollectionsModel(null)

    override fun aliased(alias: String) = TextCollectionsModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
}
