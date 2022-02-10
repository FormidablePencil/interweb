package com.idealIntent.dtos.compositions

import dtos.compositions.CompositionCategory
import dtos.space.IUserComposition
import kotlinx.serialization.Serializable

@Serializable
data class ExistingUserComposition(
    val compositionSourceId: Int,
    override val compositionCategory: CompositionCategory,
    override val compositionType: Int,
) : IUserComposition

@Serializable
data class NewUserComposition(
    override val compositionCategory: CompositionCategory,
    override val compositionType: Int,
): IUserComposition