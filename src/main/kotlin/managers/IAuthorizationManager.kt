package managers

import models.Author
import org.koin.core.component.KoinComponent

interface IAuthorizationManager: KoinComponent {
    fun validateCredentials(email: String, password: String): Author
    fun setNewPassword(password: String): Int
}