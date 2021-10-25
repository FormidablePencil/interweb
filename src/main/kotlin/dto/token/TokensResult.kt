package dto.token

import java.util.HashMap

data class TokensResult(
    val refreshToken: HashMap<String, String>,
    val accessToken: HashMap<String, String>
)