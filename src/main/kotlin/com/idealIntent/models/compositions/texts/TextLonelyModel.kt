package com.idealIntent.models.compositions.texts

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ITextLonelyEntity : Entity<ITextLonelyEntity> {
    companion object : Entity.Factory<ITextLonelyEntity>()

    val id: Int
    val text: String
}

open class TextLonelyModel(alias: String?) : Table<ITextLonelyEntity>("text_lonely", alias) {
    companion object : TextLonelyModel(null)

    override fun aliased(alias: String) = TextLonelyModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val text = varchar("text").bindTo { it.text }
}
