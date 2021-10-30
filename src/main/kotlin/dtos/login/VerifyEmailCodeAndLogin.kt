package dtos.login

data class VerifyEmailCodeAndLoginReq(
    val credential: LoginBy,
    val password: String,
    val code: String
)
