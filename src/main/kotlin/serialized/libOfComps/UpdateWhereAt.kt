package serialized.libOfComps

import dtos.libOfComps.IUpdateWhereAt
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWhereAt(override val table: Int) : IUpdateWhereAt