package managers

import configurations.interfaces.IConnectionToDb
import dtos.authorization.ResetPasswordResponse
import exceptions.ServerErrorException
import exceptions.ServerFailed
import helper.PassEncrypt
import dtos.succeeded
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import repositories.interfaces.IPasswordRepository
import repositories.interfaces.IRefreshTokenRepository

class PasswordManager(
    private val refreshTokenRepository: IRefreshTokenRepository,
    private val passwordRepository: IPasswordRepository,
    private val tokenManager: ITokenManager,
) : IPasswordManager {
    private val connectionToDb: IConnectionToDb by inject();

    override fun resetPassword(oldPassword: String, newPassword: String, authorId: Int): ResetPasswordResponse {
        validatePassword(oldPassword, authorId)

        connectionToDb.database.useTransaction {
            refreshTokenRepository.deleteOldToken(authorId)
            passwordRepository.deletePassword(authorId)
            setNewPassword(newPassword)
            val tokens = tokenManager.generateTokens(authorId)
            return ResetPasswordResponse(tokens.accessToken, tokens.refreshToken).succeeded()
        }
    }

    override fun validatePassword(password: String, authorId: Int): Boolean {
        val passwordRecord = passwordRepository.getPassword(authorId)
            ?: throw ServerErrorException(ServerFailed.FailedToRetrievePassword, this::class.java)

        return BCrypt.checkpw(password, passwordRecord.password)
    }

    override fun setNewPassword(password: String): Int {
        val encryptPassword = PassEncrypt().encryptPassword(password)
        val passwordId = passwordRepository.insertPassword(encryptPassword)

        if (passwordId !is Int)
            throw Exception("Server code. Saving password failed")

        return passwordId
    }
}