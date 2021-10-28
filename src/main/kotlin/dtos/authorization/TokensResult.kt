package dtos.authorization

import dtos.DtoResult

data class TokensResult(
    val refreshToken: HashMap<String, String>,
    val accessToken: HashMap<String, String>
) : DtoResult<TokensResultError>()

enum class TokensResultError {

}
