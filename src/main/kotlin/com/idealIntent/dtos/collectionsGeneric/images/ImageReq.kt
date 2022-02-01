package com.idealIntent.dtos.collectionsGeneric.images

import kotlinx.serialization.Serializable
import models.compositions.basicsCollections.images.IImage

@Serializable
data class ImageReq(override val description: String, override val id: Int?, override val url: String) : IImage