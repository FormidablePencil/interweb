package com.idealIntent.serialized.libOfComps

import kotlinx.serialization.Serializable

@Serializable
data class CreateComponentsRequest(
    val spaceAddress: String,
    val userComponents: List<UserComponent>,
)
