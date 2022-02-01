package com.idealIntent.models.compositions.basicCollections.videos

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IVideoToCollectionEntity : Entity<IVideoToCollectionEntity>, IWithOrder {
    companion object : Entity.Factory<IVideoToCollectionEntity>()
    val metadata: IVideoCollectionEntity
    val video: IVideoEntity
}

open class VideoToCollectionsModel(alias: String?) :
    Table<IVideoToCollectionEntity>("video_to_collection", alias) {
    companion object : VideoToCollectionsModel(null)
    override fun aliased(alias: String) = VideoToCollectionsModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val collectionId = int("collection_id").references(VideoCollectionsModel) { it.metadata }
    val video = int("video_id").references(VideosModel) { it.video }
}
