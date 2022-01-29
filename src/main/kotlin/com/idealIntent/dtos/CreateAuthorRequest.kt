package com.idealIntent.dtos

import ICreateAuthorRequest
import kotlinx.serialization.Serializable

@Serializable
class CreateAuthorRequest(
    override val email: String,
    override val firstname: String,
    override val lastname: String,
    override val password: String,
    override val username: String
) : ICreateAuthorRequest {
    init {
//        validate()
    }
}