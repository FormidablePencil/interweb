package com.idealIntent.dtos.compositionCRUD

import dtos.compositions.IUpdateWhereAt
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWhereAt(override val table: Int) : IUpdateWhereAt