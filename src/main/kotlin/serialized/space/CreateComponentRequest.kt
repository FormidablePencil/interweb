package serialized.space

import dtos.libOfComps.ComponentType
import kotlinx.serialization.Serializable

@Serializable
data class CreateComponentRequest(val componentType: ComponentType, val jsonData: String, val spaceAddress: String)