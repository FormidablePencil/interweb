package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.Image
import kotlinx.serialization.Serializable

@Serializable
data class CarouselBlurredOverlay(
    val title: String,
    val info: String,
    val navTo: String,
    val previewImages: List<Image>,
)
