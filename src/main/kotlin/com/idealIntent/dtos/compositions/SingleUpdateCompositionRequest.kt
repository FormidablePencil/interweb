package com.idealIntent.dtos.compositions

import dtos.compositions.IUpdateComposition
import kotlinx.serialization.Serializable

@Serializable
data class SingleUpdateCompositionRequest(
    override val id: Int,
    override val compositionType: Int,
    override val updateToData: RecordUpdate,
    override val where: List<UpdateWhereAt>,
) : IUpdateComposition

