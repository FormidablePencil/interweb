package com.idealIntent.dtos.compositionCRUD

import dtos.compositionCRUD.IUpdateWhereAt
import kotlinx.serialization.Serializable

// todo - remove, became obsolete by UpdateDataOfComposition
@Serializable
data class UpdateWhereAt(override val table: Int) : IUpdateWhereAt