package managers.interfaces

import dtos.authorization.LoginResult
import dtos.authorization.ResetPasswordResult
import models.profile.Author
import org.koin.core.component.KoinComponent

interface IAuthorizationManager : KoinComponent {
    fun login(email: String, password: String): LoginResult
    fun setNewPasswordForSignup(password: String): Int
    fun resetPassword(oldPassword: String, newPassword: String, authorId: Int): ResetPasswordResult
}