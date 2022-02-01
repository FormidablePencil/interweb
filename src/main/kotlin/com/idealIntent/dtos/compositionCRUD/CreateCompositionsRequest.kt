package com.idealIntent.dtos.compositionCRUD

import com.idealIntent.dtos.compositions.UserComposition
import kotlinx.serialization.Serializable

@Serializable
data class CreateCompositionsRequest(
    val spaceAddress: String,
    val userCompositions: List<UserComposition>,
)
