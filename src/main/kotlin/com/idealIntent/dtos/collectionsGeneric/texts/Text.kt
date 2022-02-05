package com.idealIntent.dtos.collectionsGeneric.texts

import kotlinx.serialization.Serializable
import models.IWithOrder
import models.IWithPK
import models.compositions.basicsCollections.texts.IText

@Serializable
data class TextPK(
    override val id: Int,
    override val orderRank: Int,
    override val text: String,
) : IText, IWithPK, IWithOrder

@Serializable
data class Text(
    override val orderRank: Int,
    override val text: String,
) : IText, IWithOrder