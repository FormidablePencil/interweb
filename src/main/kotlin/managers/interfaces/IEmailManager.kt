package managers.interfaces

import kotlinx.coroutines.CoroutineScope

interface IEmailManager {
    fun welcomeNewAuthor(authorId: Int)
    suspend fun sendResetPasswordLink(authorId: Int)
    suspend fun sendValidateEmail(authorId: Int)
}