package dtos.authorization

data class LoginResult(val authorId: Int, val tokens: TokensResult)