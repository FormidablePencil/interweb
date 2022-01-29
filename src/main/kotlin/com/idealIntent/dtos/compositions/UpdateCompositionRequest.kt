package com.idealIntent.dtos.compositions

import kotlinx.serialization.Serializable

// todo - what is this for?
@Serializable
data class UpdateCompositionRequest(val updateComposition: List<SingleUpdateCompositionRequest>)
