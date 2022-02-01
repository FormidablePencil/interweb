package com.idealIntent.models.compositions.basicCollections.texts

import models.compositions.basicsCollections.texts.IText
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ITextEntity : Entity<ITextEntity>, IText {
    companion object : Entity.Factory<ITextEntity>()
}

open class TextsModel(alias: String?) : Table<ITextEntity>("texts", alias) {
    companion object : TextsModel(null)

    override fun aliased(alias: String) = TextsModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val text = varchar("text").bindTo { it.text }
}