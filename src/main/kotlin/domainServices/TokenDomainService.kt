package domainServices

import dto.token.AuthenticateResponse
import dto.token.LoginResult
import managers.ITokenManager

class TokenDomainService(
    private val tokenManager: ITokenManager
) {

    fun login(request: AuthenticateResponse): LoginResult {
        var authorId = 1
        var refreshToken = ""
        var accessToken = ""

        tokenManager.authenticate(request.username, request.password)

        return LoginResult(authorId, refreshToken, accessToken)
    }

    fun generateToken() {
    }

    fun generateRefreshToken() {
    }

    fun refreshAccessToken() {
    }
}