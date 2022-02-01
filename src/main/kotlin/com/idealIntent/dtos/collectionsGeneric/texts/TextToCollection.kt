package com.idealIntent.dtos.collectionsGeneric.texts

import kotlinx.serialization.Serializable
import models.compositions.basicsCollections.texts.ITextToCollection

/**
 * Text to collection relationship.
 *
 * @property orderRank If order does not need to be preserve then default the value to 0.
 * @property collectionId Foreign key of [text collection][models.compositions.basicsCollections.texts.ITextCollection]
 * @property textId Foreign key of [text][models.compositions.basicsCollections.texts.IText].
 */
@Serializable
data class TextToCollection(
    override val orderRank: Int,
    override val collectionId: Int,
    override val textId: Int
) : ITextToCollection