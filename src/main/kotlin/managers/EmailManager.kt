package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.interfaces.IAppEnv
import exceptions.ServerErrorException
import managers.interfaces.IEmailManager
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import staticData.EmailMessages
import java.util.*

class EmailManager(
    private val emailVerifyCodeRepository: IEmailVerifyCodeRepository,
    private val authorRepository: IAuthorRepository,
) : IEmailManager, KoinComponent {
    private val appEnv: IAppEnv = get()
    private val eMailer: SimpleEmail = get()
    private fun emailConfig(path: String): String {
        return appEnv.getConfig("commonsMail.$path")
    }

    init {
        // setup e-mailer
        eMailer.hostName = emailConfig("hostname")
        eMailer.setSmtpPort(emailConfig("smtpPort").toInt())
        val authenticate = DefaultAuthenticator(emailConfig("username"), emailConfig("password"))
        eMailer.setAuthenticator(authenticate)
        eMailer.isSSLOnConnect = true
    }

    override fun welcomeNewAuthor(authorId: Int) {
        val author = authorRepository.getById(authorId)
            ?: throw ServerErrorException("Resource not found.", this::class.java)
        val welcomeMsg = EmailMessages.WelcomeMsg(firstname = author.firstname)

        eMailer.setFrom(emailConfig("from"))
        eMailer.subject = welcomeMsg.subject()
        eMailer.setMsg(welcomeMsg.message())
        eMailer.addTo(author.email)
        eMailer.send()
    }

    override suspend fun sendResetPasswordLink(authorId: Int) {
        val author = authorRepository.getById(authorId)
            ?: throw ServerErrorException("Resource not found", this::class.java)
        val passwordResetMsg = EmailMessages.PasswordResetMsg(username = author.username)

        // region todo - could run async
        val code = generateEmailVerificationToken(authorId)
        emailVerifyCodeRepository.insert(code)
        // endregion

        eMailer.setFrom(emailConfig("from"))
        eMailer.subject = passwordResetMsg.subject()
        eMailer.setMsg(passwordResetMsg.message())
        eMailer.addTo(author.email)
        eMailer.send()
    }

    override fun sendValidateEmail(authorId: Int) {

    }

    private fun generateEmailVerificationToken(authorId: Int): String {
        val emailValidationSecret = appEnv.getConfig("jwt.secret")
        return JWT.create()
            .withClaim("authorId", authorId)
            .withExpiresAt(Date(System.currentTimeMillis() + 2000))
            .sign(Algorithm.HMAC256(emailValidationSecret))
    }
}