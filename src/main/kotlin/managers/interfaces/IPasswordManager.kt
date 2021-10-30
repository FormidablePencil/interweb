package managers.interfaces

import dtos.authorization.ResetPasswordResponse
import org.koin.core.component.KoinComponent

interface IPasswordManager: KoinComponent {
    fun resetPassword(oldPassword: String, newPassword: String, authorId: Int): ResetPasswordResponse
    fun validatePassword(password: String, authorId: Int): Boolean
    fun setNewPassword(password: String): Int
}