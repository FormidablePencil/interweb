package managers.interfaces

interface IEmailManager {
    fun sendResetPassword(userId: Int)
    fun sendCreatedAccount(userId: Int)
}