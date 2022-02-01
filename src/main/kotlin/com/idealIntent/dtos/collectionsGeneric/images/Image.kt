package com.idealIntent.dtos.collectionsGeneric.images

import kotlinx.serialization.Serializable
import models.IWithOrder
import models.compositions.basicsCollections.images.IImage

/**
 * Image dto.
 *
 * @property url
 * @property description
 * @constructor Create empty Image
 */
@Serializable
data class Image(
    override val id: Int?,
    override val orderRank: Int,
    override val url: String,
    override val description: String,
) : IImage, IWithOrder
