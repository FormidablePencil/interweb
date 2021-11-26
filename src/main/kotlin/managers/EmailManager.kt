package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.interfaces.IAppEnv
import exceptions.ServerErrorException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import managers.interfaces.IEmailManager
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailRepository
import staticData.EmailMessages
import java.util.*

class EmailManager(
    private val emailRepository: IEmailRepository,
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

    override suspend fun sendResetPasswordLink(authorId: Int): Unit = coroutineScope {
        val author = authorRepository.getById(authorId)
            ?: throw ServerErrorException("Resource not found", this::class.java)
        val passwordResetMsg = EmailMessages.PasswordResetMsg(username = author.username)

        launch {
            val code = generateCode(authorId)
            emailRepository.insertResetPasswordCode(code, authorId)
        }

        eMailer.setFrom(emailConfig("from"))
        eMailer.subject = passwordResetMsg.subject()
        eMailer.setMsg(passwordResetMsg.message())
        eMailer.addTo(author.email)
        eMailer.send()
    }

    override suspend fun sendValidateEmail(authorId: Int): Unit = coroutineScope {
        val author = authorRepository.getById(authorId)
            ?: throw ServerErrorException("Resource not found", this::class.java)
        val passwordResetMsg = EmailMessages.ValidateEmailMsg(username = author.username)

        launch {
            val code = generateCode(authorId)
            emailRepository.insertEmailVerificationCode(code, authorId)
        }

        eMailer.setFrom(emailConfig("from"))
        eMailer.subject = passwordResetMsg.subject()
        eMailer.setMsg(passwordResetMsg.message())
        eMailer.addTo(author.email)
        eMailer.send()
    }

    private fun generateCode(authorId: Int): String {
        val emailValidationSecret = appEnv.getConfig("jwt.emailSecret")
        return JWT.create()
            .withClaim("authorId", authorId)
            .withExpiresAt(Date(System.currentTimeMillis() + 2000))
            .sign(Algorithm.HMAC256(emailValidationSecret))
    }
}