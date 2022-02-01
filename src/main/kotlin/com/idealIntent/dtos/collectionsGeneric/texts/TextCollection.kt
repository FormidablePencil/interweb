package com.idealIntent.dtos.collectionsGeneric.texts

import kotlinx.serialization.Serializable

@Serializable
data class TextCollection(val label: String, val texts: List<Text>)