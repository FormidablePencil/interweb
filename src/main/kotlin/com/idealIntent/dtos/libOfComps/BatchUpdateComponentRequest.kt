package com.idealIntent.dtos.libOfComps

import dtos.libOfComps.IBatchUpdateComponent
import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateComponentRequest(
    override val componentType: Int,
    override val componentId: Int,
    override val updateToData: List<RecordUpdate>,
    override val where: List<UpdateWhereAt>,
) : IBatchUpdateComponent