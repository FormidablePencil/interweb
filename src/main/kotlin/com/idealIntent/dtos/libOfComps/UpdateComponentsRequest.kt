package com.idealIntent.dtos.libOfComps

import kotlinx.serialization.Serializable

@Serializable
data class UpdateComponentsRequest(val updateComponent: List<SingleUpdateComponentRequest>)
