package serialized.space

import dtos.libOfComps.ComponentType
import kotlinx.serialization.Serializable

@Serializable
data class CreateComponentRequest(
    val spaceAddress: String,
    val createComponent: CreateComponent,
)

@Serializable
data class CreateComponent(
    val componentType: ComponentType,
    val jsonData: String,
)
