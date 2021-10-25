package managers

import models.Author

interface IAuthorizationManager {
    fun validateCredentials(email: String, password: String): Author
    fun setNewPassword(password: String): Int
}