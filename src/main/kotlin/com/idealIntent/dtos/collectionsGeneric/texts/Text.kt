package com.idealIntent.dtos.collectionsGeneric.texts

import kotlinx.serialization.Serializable
import models.compositions.basicsCollections.texts.IText

@Serializable
data class Text(override val text: String, override val orderRank: Int) : IText
