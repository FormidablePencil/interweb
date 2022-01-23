package serialized.space

import kotlinx.serialization.Serializable

@Serializable
data class CreateComponentRequest(val componentType: Int, val jsonData: String, val spaceAddress: String)