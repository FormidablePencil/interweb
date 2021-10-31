package dtos.authorization

import dtos.ApiDataResponse
import dtos.IApiResponseEnum
import dtos.token.responseData.TokenResponseData
import io.ktor.http.*

class TokensResponse : ApiDataResponse<TokenResponseData, G, TokensResponse>(G)

internal typealias G = TokensResponseFailed

enum class TokensResponseFailed {
    InvalidRefreshToken;

    companion object : IApiResponseEnum<G> {
        override fun getMsg(code: G): String {
            return when (code) {
                InvalidRefreshToken -> "Invalid refresh token."
            }
        }

        override fun getStatusCode(code: G): HttpStatusCode {
            return when (code) {
                InvalidRefreshToken -> HttpStatusCode.BadRequest
            }
        }
    }
}