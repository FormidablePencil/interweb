package dto.token

data class LoginResult(val authorId: Int, val refreshToken: String, val accessToken: String)