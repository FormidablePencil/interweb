package com.idealIntent.dtos.compositions

import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateCompositionsRequest(val updateComposition: List<BatchUpdateCompositionRequest>)