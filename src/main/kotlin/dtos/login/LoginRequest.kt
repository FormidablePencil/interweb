package dtos.login

data class LoginRequest(val credential: String, val password: String, val loginBy: LoginBy)

enum class LoginBy {
    Username, Email
}

