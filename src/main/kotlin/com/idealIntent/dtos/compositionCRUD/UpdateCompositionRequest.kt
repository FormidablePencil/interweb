package com.idealIntent.dtos.compositionCRUD

import kotlinx.serialization.Serializable

// todo - what is this for?
@Serializable
data class UpdateCompositionRequest(val updateComposition: List<SingleUpdateCompositionRequest>)
