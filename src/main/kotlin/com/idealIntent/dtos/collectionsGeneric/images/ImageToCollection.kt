package com.idealIntent.dtos.collectionsGeneric.images

import kotlinx.serialization.Serializable
import models.compositions.basicsCollections.images.IImageToCollection

/**
 * Image to collection relationship.
 *
 * @property orderRank If order does not need to be preserve then default the value to 0.
 * @property collectionId Foreign key of [Image collection][models.compositions.basicsCollections.images.IImageCollection]
 * @property imageId Foreign key of [Image][models.compositions.basicsCollections.images.IImage].
 */
@Serializable
data class ImageToCollection(
    override val orderRank: Int,
    override val collectionId: Int,
    override val imageId: Int
) : IImageToCollection