package dtos.login

data class LoginReq(val credential: String, val password: String, val loginBy: LoginBy)

enum class LoginBy {
    Username, Email
}

