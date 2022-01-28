package com.idealIntent.serialized.space

import dtos.space.IGetSpaceRequest
import kotlinx.serialization.Serializable

@Serializable
class GetSpaceRequest(override val address: String): IGetSpaceRequest {
    init {
        validate()
    }
}