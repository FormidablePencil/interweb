package serialized.space

import kotlinx.serialization.Serializable

@Serializable
data class BatchUpdateComponentsRequest(val updateComponent: List<BatchUpdateComponentRequest>)