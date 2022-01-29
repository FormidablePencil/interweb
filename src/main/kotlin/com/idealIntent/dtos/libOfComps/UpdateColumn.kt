package com.idealIntent.dtos.libOfComps

import dtos.libOfComps.IUpdateColumn
import kotlinx.serialization.Serializable

@Serializable
data class UpdateColumn(override val column: Int, override val value: String) : IUpdateColumn