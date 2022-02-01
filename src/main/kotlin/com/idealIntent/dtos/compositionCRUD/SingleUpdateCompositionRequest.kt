package com.idealIntent.dtos.compositionCRUD

import dtos.compositionCRUD.IUpdateComposition
import kotlinx.serialization.Serializable

@Serializable
data class SingleUpdateCompositionRequest(
    override val id: Int,
    override val compositionType: Int,
    override val updateToData: RecordUpdate,
    override val where: List<UpdateWhereAt>,
) : IUpdateComposition

