package managers

import configurations.AppEnv
import dtos.authorization.ResetPasswordResponse
import dtos.succeeded
import exceptions.ServerErrorException
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import repositories.PasswordRepository
import repositories.RefreshTokenRepository

class PasswordManager(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordRepository: PasswordRepository,
    private val tokenManager: TokenManager,
) : KoinComponent {
    private val appEnv: AppEnv by inject()

    fun resetPassword(oldPassword: String, newPassword: String, authorId: Int): ResetPasswordResponse {
        validatePassword(oldPassword, authorId)

        appEnv.database.useTransaction {
            refreshTokenRepository.deleteOldToken(authorId)
            passwordRepository.deletePassword(authorId)
            if (setNewPassword(newPassword, authorId))
                throw ServerErrorException("Server code. Saving password failed", this::class.java)
            val tokens = tokenManager.generateTokens(authorId)
            return ResetPasswordResponse(tokens.accessToken, tokens.refreshToken)
                .succeeded(HttpStatusCode.MultiStatus) // todo change implementation
        }
    }

    fun validatePassword(password: String, authorId: Int): Boolean {
        val passwordRecord = passwordRepository.getPassword(authorId)
            ?: throw ServerErrorException("Failed to retrieve password", this::class.java)

        return BCrypt.checkpw(password, passwordRecord.password)
    }

    fun setNewPassword(password: String, authorId: Int): Boolean {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        return passwordRepository.insertPassword(passwordHash, authorId)
    }
}