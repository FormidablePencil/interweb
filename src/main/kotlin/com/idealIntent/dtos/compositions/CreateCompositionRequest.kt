package com.idealIntent.dtos.compositions

import dtos.compositions.CompositionType
import dtos.space.IUserComposition
import kotlinx.serialization.Serializable

@Serializable
data class CreateCompositionRequest(
    val spaceAddress: String,
    val userComposition: IUserComposition,
)

@Serializable
data class UserComposition(
    override val compositionType: CompositionType,
    override val jsonData: String,
) : IUserComposition
