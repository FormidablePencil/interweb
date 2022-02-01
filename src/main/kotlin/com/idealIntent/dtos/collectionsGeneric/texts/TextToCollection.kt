package com.idealIntent.dtos.collectionsGeneric.texts

import kotlinx.serialization.Serializable
import models.compositions.basicsCollections.texts.ITextToCollection

@Serializable
data class TextToCollection(
    override val orderRank: Int,
    override val collectionId: Int,
    override val textId: Int
) : ITextToCollection