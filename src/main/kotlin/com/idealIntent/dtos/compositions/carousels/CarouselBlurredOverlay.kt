package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.Image
import dtos.compositions.carousels.ICarouselBlurredOverlay
import kotlinx.serialization.Serializable

@Serializable
data class CarouselBlurredOverlay(
    override val info: String,
    override val navTo: String,
    override val previewImages: List<Image>,
    override val title: String
) : ICarouselBlurredOverlay
