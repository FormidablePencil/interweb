package com.idealIntent.dtos.collectionsGeneric.texts

import kotlinx.serialization.Serializable
import models.IWithOrder
import models.compositions.basicsCollections.texts.IText

// todo - null or int
@Serializable
data class Text(
    override var id: Int?,
    override val orderRank: Int,
    override val text: String,
) : IText, IWithOrder
