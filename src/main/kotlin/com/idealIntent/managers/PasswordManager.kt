package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.auth.ResetPasswordResponse
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.TempException
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt

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
            ?: throw TempException("Failed to retrieve password.", this::class.java)

        return BCrypt.checkpw(password, passwordRecord.passwordHash)
    }

    fun setNewPassword(password: String, authorId: Int) {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        if (!passwordRepository.insert(passwordHash, authorId))
            throw TempException("Server code. Saving password failed.", this::class.java)
    }
}