package managers

import managers.interfaces.IEmailManager
import managers.interfaces.ITokenManager

class EmailManager(
    private val tokenManager: ITokenManager
) : IEmailManager {

    override fun sendResetPassword(userId: Int) {
        TODO()
    }

    override fun sendValidateEmail(email: String) {
        TODO()
        val authorId = 0
        val tokens = tokenManager.generateTokens(authorId)
    }
}