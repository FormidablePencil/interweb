package serialized.space

import kotlinx.serialization.Serializable

@Serializable
data class WhereIsComponentToUpdate(
//    override val column: Int,
    override val table: Int
) : IWhereIsComponentToUpdate