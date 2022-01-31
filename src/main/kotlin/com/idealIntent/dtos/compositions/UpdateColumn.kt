package com.idealIntent.dtos.compositions

import dtos.compositions.IUpdateColumn
import kotlinx.serialization.Serializable

@Serializable
data class UpdateColumn(override val column: Int, override val value: String) : IUpdateColumn