package managers.interfaces

interface IEmailManager {
    fun sendResetPassword(userId: Int)
    fun welcomeNewAuthor(email: String)
}