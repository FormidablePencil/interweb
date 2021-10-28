package dtos.authorization

import dtos.DtoResult

data class ResetPasswordResult(val refreshToken: String?, val accessToken: String?) : DtoResult<TokensResultError>()