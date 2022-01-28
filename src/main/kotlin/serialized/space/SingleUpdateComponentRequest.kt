package serialized.space

import kotlinx.serialization.Serializable
import repositories.components.RecordUpdate

@Serializable
data class SingleUpdateComponentRequest(
    override val componentType: Int,
    override val componentId: Int,
    override val updateToData: RecordUpdate,
    override val where: List<WhereIsComponentToUpdate>, // list just in case data is nested multiple levels
) : IUpdateComponent

