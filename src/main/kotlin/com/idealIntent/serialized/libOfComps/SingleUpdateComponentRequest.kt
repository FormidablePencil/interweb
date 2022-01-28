package com.idealIntent.serialized.libOfComps

import dtos.libOfComps.IUpdateComponent
import kotlinx.serialization.Serializable

@Serializable
data class SingleUpdateComponentRequest(
    override val componentType: Int,
    override val componentId: Int,
    override val updateToData: RecordUpdate,
    override val where: List<UpdateWhereAt>,
) : IUpdateComponent

