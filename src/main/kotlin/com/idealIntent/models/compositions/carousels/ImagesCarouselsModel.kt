package com.idealIntent.models.compositions.carousels

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IImagesCarousel {
    val id: Int
    val name: String // todo remove
    val imageCollectionId: Int
    val redirectTextCollectionId: Int
}

/**
 * SpaceResponseFailed carousel composition. Composes of a collection of images and a collection of redirect links which is
 * mapped over images. When an image of carousel is click the user will be redirected to another site.
 */
interface IImagesCarouselEntity : Entity<IImagesCarouselEntity>, IImagesCarousel {
    companion object : Entity.Factory<IImagesCarouselEntity>()

    val compositionType: Int // todo - add this to interface
}

open class ImagesCarouselsModel(alias: String?) : Table<IImagesCarouselEntity>("image_carousels", alias) {
    companion object : ImagesCarouselsModel(null)

    override fun aliased(alias: String) = ImagesCarouselsModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val imageCollectionId = int("image_collection_id").bindTo { it.imageCollectionId }
    val redirectTextCollectionId = int("redirect_text_collection_id").bindTo { it.redirectTextCollectionId }
}