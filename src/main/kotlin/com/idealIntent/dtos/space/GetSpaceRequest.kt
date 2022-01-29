package com.idealIntent.dtos.space

import dtos.space.IGetSpaceRequest
import kotlinx.serialization.Serializable

@Serializable
class GetSpaceRequest(override val address: String): IGetSpaceRequest {
    init {
        validate()
    }
}