package serialized.space

import dtos.space.ICreateSpaceRequest
import kotlinx.serialization.Serializable

@Serializable //todo - unnecessary jsonData, delete
class CreateSpaceRequest(override val authorId: Int, override val jsonData: String): ICreateSpaceRequest {
    init {
        validate()
    }
}