package com.idealIntent.dtos.compositionCRUD

import dtos.compositionCRUD.IBatchUpdateComposition
import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateCompositionRequest(
    override val id: Int,
    override val compositionType: Int,
    override val updateToData: List<RecordUpdate>,
    override val where: List<UpdateWhereAt>,
) : IBatchUpdateComposition