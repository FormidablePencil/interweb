package services

import dtos.authorization.LoginResult
import dtos.authorization.TokensResult
import managers.interfaces.IAuthorizationManager
import managers.interfaces.ITokenManager
import repositories.interfaces.ITokenRepository

class AuthorizationService(
    private val tokenManager: ITokenManager,
    private val tokenRepository: ITokenRepository,
    private val authorizationManager: IAuthorizationManager,
) {
    fun login(email: String, password: String): LoginResult {
        return authorizationManager.login(email, password)
    }

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResult {
        return tokenManager.refreshAccessToken(refreshToken, authorId)
    }

    // ideally, a confirmation should be sent to mail and the link to reset password
    fun resetPasswordByEmail() {

//        authorizationManager.resetPassword(oldPassword, newPassword, authorId)
//        authorizationManager.setNewPasswordForSignup()
//        tokenRepository.deleteOldTokens()
    }
}

// tokens must be saved in db
// tokens must be sent from the client via bearer
// and validated everytime before giving access to data
