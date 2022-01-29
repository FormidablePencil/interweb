package com.idealIntent.dtos.libOfComps

import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateComponentsRequest(val updateComponent: List<BatchUpdateComponentRequest>)