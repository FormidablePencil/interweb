package serialized.space

import kotlinx.serialization.Serializable

@Serializable
data class UpdateComponentsRequest(val updateComponent: List<SingleUpdateComponentRequest>)
