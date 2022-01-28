package serialized.space

import kotlinx.serialization.Serializable
import repositories.components.RecordUpdate

@Serializable
data class BatchUpdateComponentRequest(
    override val componentType: Int,
    override val componentId: Int,
    override val updateToData: List<RecordUpdate>,
    override val where: List<WhereIsComponentToUpdate>,
) : IBatchUpdateComponent