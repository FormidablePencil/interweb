package managers

import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import dtos.authorization.LoginResult
import dtos.authorization.LoginResultError
import dtos.authorization.ResetPasswordResult
import helper.PassEncrypt
import helper.failed
import helper.succeeded
import managers.interfaces.IAuthorizationManager
import managers.interfaces.ITokenManager
import models.profile.Author
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IPasswordRepository
import repositories.interfaces.ITokenRepository

class AuthorizationManager(
    private val authorRepository: IAuthorRepository,
    private val passwordRepository: IPasswordRepository,
    private val passEncrypt: PassEncrypt,
    private val tokenManager: ITokenManager,
    private val tokenRepository: ITokenRepository,

    ) : IAuthorizationManager {
    private val appEnv: IAppEnv by inject()
    private val connectionToDb: IConnectionToDb by inject();

    override fun login(email: String, password: String): LoginResult {
        val errorResponseMessage = "Invalid credentials"

        val author: Author? = authorRepository.getByEmail(email)
        if (author?.id == null)
            return LoginResult().failed(LoginResultError.InvalidEmail, errorResponseMessage)

        return if (validatePassword(password, author.id))
            LoginResult(author.id).succeeded()
        else LoginResult().failed(LoginResultError.InvalidPassword, errorResponseMessage)
    }

    override fun setNewPasswordForSignup(password: String): Int {
        return setNewPassword(password)
    }

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

    private fun validatePassword(password: String, authorId: Int): Boolean {
        val passwordRecord = passwordRepository.getPassword(authorId)

        val passwordHash = passwordRecord?.password;

        return BCrypt.checkpw(passwordHash, password)
    }

    private fun setNewPassword(password: String): Int {
        val encryptPassword = passEncrypt.encryptPassword(password)
        val passwordId = passwordRepository.insertPassword(encryptPassword)

        if (passwordId !is Int)
            throw Exception("Server error. Saving password failed")

        return passwordId
    }
}