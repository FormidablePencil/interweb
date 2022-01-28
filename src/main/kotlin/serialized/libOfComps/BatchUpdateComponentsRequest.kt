package serialized.libOfComps

import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateComponentsRequest(val updateComponent: List<BatchUpdateComponentRequest>)