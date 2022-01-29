package com.idealIntent.dtos.compositions

import kotlinx.serialization.Serializable

@Serializable
data class CreateCompositionsRequest(
    val spaceAddress: String,
    val userCompositions: List<UserComposition>,
)
