package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import dtos.authorization.ResetPasswordResponse
import dtos.succeeded
import com.idealIntent.exceptions.ServerErrorException
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository

// todo - comments
class PasswordManager(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordRepository: PasswordRepository,
    private val tokenManager: TokenManager,
) : KoinComponent {
    private val appEnv: AppEnv by inject()

    fun changePassword(currentPassword: String, newPassword: String, authorId: Int): ResetPasswordResponse {
        validatePassword(currentPassword, authorId)

        appEnv.database.useTransaction {
            refreshTokenRepository.delete(authorId)
            passwordRepository.delete(authorId)
            setNewPassword(newPassword, authorId)
            val tokens = tokenManager.generateAuthTokens(authorId)
            return ResetPasswordResponse().succeeded(HttpStatusCode.OK, tokens)
        }
    }

    fun validatePassword(password: String, authorId: Int): Boolean {
        val passwordRecord = passwordRepository.get(authorId)
            ?: throw ServerErrorException("Failed to retrieve password.", this::class.java)

        return BCrypt.checkpw(password, passwordRecord.password)
    }

    fun setNewPassword(password: String, authorId: Int) {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        if (!passwordRepository.insert(passwordHash, authorId))
            throw ServerErrorException("Server code. Saving password failed.", this::class.java)
    }
}