package managers.interfaces

interface IEmailManager {
    fun sendResetPassword(userId: Int)
    fun sendValidateEmail(userId: Int)
}