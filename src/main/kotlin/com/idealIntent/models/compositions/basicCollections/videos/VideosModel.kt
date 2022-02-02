package com.idealIntent.models.compositions.basicCollections.videos

import models.compositions.basicsCollections.videos.IVideo
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface IVideoEntity : Entity<IVideoEntity>, IVideo {
    companion object : Entity.Factory<IVideoEntity>()

    val id: Int
}

object VideosModel : Table<IVideoEntity>("videos") {
    val id = int("id").primaryKey().bindTo { it.id }
    val url = varchar("url").bindTo { it.url }
    val title = varchar("name").bindTo { it.title }
    val description = varchar("description").bindTo { it.description }
}