package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import managers.interfaces.IEmailManager
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import java.util.*

class EmailManager(
    private val emailValidationCodeRepository: IEmailVerifyCodeRepository,
    private val authorRepository: IAuthorRepository,
) : IEmailManager {

    override fun sendResetPassword(userId: Int) {
        TODO()
    }

    override fun welcomeNewAuthor(email: String) {
//        val code = genEmailVerifyCode(email)
//        emailValidationCodeRepository.insert(code)
        // TODO("create top level logic for it")
    }

    private fun genEmailVerifyCode(email: String): String {
        val emailValidationSecret = "dfd"
        val request = authorRepository.getByEmail(email)

        return JWT.create()
            .withClaim("authorId", request?.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 2000))
            .sign(Algorithm.HMAC256(emailValidationSecret))
    }
}