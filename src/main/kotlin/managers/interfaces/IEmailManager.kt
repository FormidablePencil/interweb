package managers.interfaces

interface IEmailManager {
    fun welcomeNewAuthor(authorId: Int)
    suspend fun sendResetPasswordLink(authorId: Int)
    fun sendValidateEmail(authorId: Int)
}