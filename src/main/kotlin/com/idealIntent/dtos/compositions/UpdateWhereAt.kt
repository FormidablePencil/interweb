package com.idealIntent.dtos.compositions

import dtos.compositions.IUpdateWhereAt
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWhereAt(override val table: Int) : IUpdateWhereAt