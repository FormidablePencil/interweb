package managers

import configurations.interfaces.IConnectionToDb
import dtos.authorization.ResetPasswordResult
import helper.PassEncrypt
import helper.succeeded
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import repositories.interfaces.IPasswordRepository
import repositories.interfaces.ITokenRepository

class PasswordManager(
    private val tokenRepository: ITokenRepository,
    private val passwordRepository: IPasswordRepository,
    private val tokenManager: ITokenManager,
) : IPasswordManager {
    private val connectionToDb: IConnectionToDb by inject();

    override fun resetPassword(oldPassword: String, newPassword: String, authorId: Int): ResetPasswordResult {
        validatePassword(oldPassword, authorId)

        connectionToDb.database.useTransaction {
            tokenRepository.deleteOldTokens(authorId)
            passwordRepository.deletePassword(authorId)
            setNewPassword(newPassword)
            val tokens = tokenManager.genTokensOnResetPassword(authorId)
            return ResetPasswordResult(tokens.accessToken, tokens.refreshToken).succeeded()
        }
    }

    override fun validatePassword(password: String, authorId: Int): Boolean {
        val passwordRecord = passwordRepository.getPassword(authorId)

        val passwordHash = passwordRecord?.password;

        return BCrypt.checkpw(passwordHash, password)
    }

    override fun setNewPassword(password: String): Int {
        val encryptPassword = PassEncrypt().encryptPassword(password)
        val passwordId = passwordRepository.insertPassword(encryptPassword)

        if (passwordId !is Int)
            throw Exception("Server error. Saving password failed")

        return passwordId
    }
}