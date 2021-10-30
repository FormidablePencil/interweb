package dtos.login

data class VerifyEmailCodeThenLoginReq(
    val credential: String,
    val loginBy: LoginBy,
    val password: String,
    val code: String
)
