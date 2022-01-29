package com.idealIntent.dtos.compositions

import dtos.compositions.CompositionCategory
import dtos.space.IUserComposition
import kotlinx.serialization.Serializable

@Serializable
data class CreateCompositionRequest(
    val spaceAddress: String,
    val userComposition: IUserComposition,
)

@Serializable
data class UserComposition(
    override val compositionType: CompositionCategory,
    override val jsonData: String,
) : IUserComposition
