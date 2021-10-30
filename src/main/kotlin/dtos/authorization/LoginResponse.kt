package dtos.authorization

import dtos.ApiResponse

data class LoginResponse(val tokens: TokensResponse? = null) : ApiResponse<LoginResponseFailed>()

enum class LoginResponseFailed {
    InvalidEmail, InvalidUsername, InvalidPassword, InvalidEmailVerificationCode;

    companion object {
        fun getMsg(enum: LoginResponseFailed): String {
            return when (enum) {
                InvalidEmail, InvalidPassword -> "Invalid credentials."
                FirstTimeEmailVerificationRequired -> "z"
            }
        }
    }
}
