package com.idealIntent.models.compositions.banners

import models.compositions.banners.IImageBanner
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface IBannerImageEntity : Entity<IBannerImageEntity>, IImageBanner {
    companion object : Entity.Factory<IBannerImageEntity>()

    val id: Int
    val imageUrl: String
    val imageAlt: String
}

open class BannerImageModel(alias: String?) : Table<IBannerImageEntity>("banner_image", alias) {
    companion object : BannerImageModel(null)

    override fun aliased(alias: String) = BannerImageModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val imageUrl = varchar("image_url").bindTo { it.imageUrl }
    val imageAlt = varchar("imageAlt").bindTo { it.imageAlt }
}