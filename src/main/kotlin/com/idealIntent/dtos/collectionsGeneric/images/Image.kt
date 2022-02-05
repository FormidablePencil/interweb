package com.idealIntent.dtos.collectionsGeneric.images

import kotlinx.serialization.Serializable
import models.IWithOrder
import models.IWithPK
import models.compositions.basicsCollections.images.IImage

@Serializable
data class ImagePK(
    override val id: Int,
    override val orderRank: Int,
    override val url: String,
    override val description: String,
) : IImage, IWithPK, IWithOrder

@Serializable
data class Image(
    override val orderRank: Int,
    override val url: String,
    override val description: String,
) : IImage, IWithOrder
