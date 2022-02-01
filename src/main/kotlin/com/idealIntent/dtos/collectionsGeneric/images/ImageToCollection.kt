package com.idealIntent.dtos.collectionsGeneric.images

import kotlinx.serialization.Serializable
import models.compositions.basicsCollections.images.IImageToCollection

@Serializable
data class ImageToCollection(
    override val orderRank: Int,
    override val collectionId: Int,
    override val imageId: Int
) : IImageToCollection