package com.idealIntent.dtos.compositionCRUD

import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateCompositionsRequest(val updateComposition: List<BatchUpdateCompositionRequest>)