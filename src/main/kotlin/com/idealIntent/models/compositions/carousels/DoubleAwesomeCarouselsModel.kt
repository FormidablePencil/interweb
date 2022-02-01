package com.idealIntent.models.compositions.carousels

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IDoubleAwesomeCarouselEntity : Entity<IDoubleAwesomeCarouselEntity> {
    companion object : Entity.Factory<IDoubleAwesomeCarouselEntity>()

    val id: Int
    val videoCollectionId: Int
    val imageCollectionId: Int
    val privilegesId: Int
}

object DoubleAwesomeCarouselsModel : Table<IDoubleAwesomeCarouselEntity>("double_awesome_carousels") {
    val id = int("id").primaryKey().bindTo { it.id }
    val videoCollectionId = int("video_collection_id").bindTo { it.videoCollectionId }
    val imageCollectionId = int("image_collection_id").bindTo { it.imageCollectionId }
    val privilegesId = int("privileges_id").bindTo { it.privilegesId }
}