package domainServices

import managers.interfaces.ITokenManager

class AuthorizationService(
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