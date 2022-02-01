package com.idealIntent.models.compositions.banners

import models.compositions.banners.IImageBanner
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IImageBannerEntity : Entity<IImageBannerEntity>, IImageBanner {
    companion object : Entity.Factory<IImageBannerEntity>()

    val id: Int
}

open class ImageBannersModel(alias: String?) : Table<IImageBannerEntity>("image_banners", alias) {
    companion object : ImageBannersModel(null)

    override fun aliased(alias: String) = ImageBannersModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
}