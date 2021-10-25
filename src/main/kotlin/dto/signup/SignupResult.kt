package dto.signup

import dto.token.TokensResult

data class SignupResult(val authorId: Int, val tokens: TokensResult)