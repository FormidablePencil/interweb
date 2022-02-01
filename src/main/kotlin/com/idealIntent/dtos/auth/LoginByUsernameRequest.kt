package com.idealIntent.dtos.auth

import dtos.login.ILoginByUsernameRequest
import kotlinx.serialization.Serializable

//@Serializable
class LoginByUsernameRequest(override val username: String, override val password: String) : ILoginByUsernameRequest {
    init {
        validate()
    }
}