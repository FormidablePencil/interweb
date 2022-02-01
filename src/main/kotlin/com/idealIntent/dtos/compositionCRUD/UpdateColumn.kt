package com.idealIntent.dtos.compositionCRUD

import dtos.compositionCRUD.IUpdateColumn
import kotlinx.serialization.Serializable

@Serializable
data class UpdateColumn(override val column: Int, override val value: String) : IUpdateColumn