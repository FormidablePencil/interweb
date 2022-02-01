package com.idealIntent.dtos.compositionCRUD

import dtos.compositionCRUD.IUpdateWhereAt
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWhereAt(override val table: Int) : IUpdateWhereAt