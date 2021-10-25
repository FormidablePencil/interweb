package domainServices

import managers.ITokenManager

class TokenDomainService(
    private val tokenManager: ITokenManager
) {

    fun generateToken() {
    }

    fun generateRefreshToken() {
    }

    fun refreshAccessToken(refreshToken: String): Pair<String, String> {
        return tokenManager.refreshAccessToken(refreshToken)
    }
}