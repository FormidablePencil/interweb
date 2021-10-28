package dto.token

import dto.Result

data class TokensResult(
    val refreshToken: HashMap<String, String>,
    val accessToken: HashMap<String, String>
) : Result<TokensResultError>()

enum class TokensResultError {

}
