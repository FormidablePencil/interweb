package com.idealIntent.models.compositions.carousels

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ICarouselBlurredOverlayModelEntity : Entity<ICarouselBlurredOverlayModelEntity> {
    companion object : Entity.Factory<ICarouselBlurredOverlayModelEntity>()

    val id: Int
    val imageCollectionId: Int
    val textCollectionId: Int
}

open class CarouselBlurredOverlayModel(alias: String?) :
    Table<ICarouselBlurredOverlayModelEntity>("Carousel_blurred_overlay", alias) {
    companion object : CarouselBlurredOverlayModel(null)

    override fun aliased(alias: String) = CarouselBlurredOverlayModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val imageCollectionId = int("image_collection_id").bindTo { it.imageCollectionId }
    val textCollectionId = int("redirect_text_collection_id").bindTo { it.textCollectionId }
}
