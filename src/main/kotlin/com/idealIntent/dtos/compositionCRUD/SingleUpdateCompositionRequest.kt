package com.idealIntent.dtos.compositionCRUD

import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.CompositionCategory
import dtos.space.IUserComposition
import kotlinx.serialization.Serializable

@Serializable
data class SingleUpdateCompositionRequest(
    val compositionSourceId: Int,
    override val compositionCategory: CompositionCategory,
    override val compositionType: Int,
    val compositionUpdateQue: List<UpdateDataOfComposition>,
    val authorId: Int,
) : IUserComposition