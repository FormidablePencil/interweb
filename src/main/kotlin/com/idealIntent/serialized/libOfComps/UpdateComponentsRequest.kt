package com.idealIntent.serialized.libOfComps

import kotlinx.serialization.Serializable

@Serializable
data class UpdateComponentsRequest(val updateComponent: List<SingleUpdateComponentRequest>)
