package com.idealIntent.dtos.collectionsGeneric.images

import kotlinx.serialization.Serializable

@Serializable
data class ImageCollection(val id: Int?, val images: List<Image>)