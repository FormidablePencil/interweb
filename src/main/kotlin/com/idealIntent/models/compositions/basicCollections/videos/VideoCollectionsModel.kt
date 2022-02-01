package com.idealIntent.models.compositions.basicCollections.videos

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IVideoCollectionEntity : Entity<IVideoCollectionEntity> {
    companion object : Entity.Factory<IVideoCollectionEntity>()

    val id: Int
}

object VideoCollectionsModel : Table<IVideoCollectionEntity>("video_collections") {
    val id = int("id").primaryKey().bindTo { it.id }
}