package managers

import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.koin.KoinListener
import io.ktor.config.*
import io.mockk.*
import models.profile.Author
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import org.koin.test.KoinTest
import repositories.AuthorRepository
import repositories.EmailRepository
import shared.appEnvMockHelper
import staticData.EmailMessages
import staticData.IEmailMsgStructure

class EmailManagerTest : BehaviorSpec(), KoinTest {
    private val appEnv = mockk<AppEnv>()
    private val eMailer: SimpleEmail = mockk(relaxed = true)

    override fun listeners() = listOf(
        KoinListener(
            module {
                single { appEnv }
                single { eMailer }
            }
        )
    )

    init {
        val emailRepository: EmailRepository = mockk(relaxed = true)
        val authorRepository: AuthorRepository = mockk()
        val authorId = 0
        val author: Author = mockk()
        val email = "someEmailAddress@gmail.com21"
        val username = "Username"
        val firstname = "Firstname"
        val configs = HoconApplicationConfig(ConfigFactory.load())

        lateinit var emailManager: EmailManager

        fun verifyInstantiation() = verifyOrder {
            appEnv.getConfig("commonsMail.hostname")
            appEnv.getConfig("commonsMail.smtpPort")
            appEnv.getConfig("commonsMail.username")
            appEnv.getConfig("commonsMail.password")
        }

        beforeEach {
            clearAllMocks()

            appEnvMockHelper(appEnv)

            every { author.firstname } returns firstname
            every { author.email } returns email
            every { author.username } returns username
            every { author.id } returns authorId
            every { authorRepository.getById(authorId) } returns author

            emailManager = spyk(EmailManager(emailRepository, authorRepository), recordPrivateCalls = true)

            verifyInstantiation()
        }

        fun sendEmailVerificationOrder(
            emailMsg: IEmailMsgStructure,
            emailFrom: String = configs.property("commonsMail.from").getString()
        ) {
            appEnv.getConfig("commonsMail.from")
            eMailer.setFrom(emailFrom)
            eMailer.subject = emailMsg.subject()
            eMailer.setMsg(emailMsg.message())
            eMailer.addTo(email)
        }

        xgiven("sendResetPasswordLink") {
            then("just send reset password link to email on file") {
                val passwordResetMsg = EmailMessages.PasswordResetMsg(username = author.username)

                emailManager.sendResetPasswordLink(authorId)

                verifyOrder {
                    authorRepository.getById(authorId)
                    sendEmailVerificationOrder(passwordResetMsg)
                }
                coVerify {
                    appEnv.getConfig("jwt.emailSecret")
                    emailRepository.insertResetPasswordCode(any(), authorId)
                }
            }
        }

        given("welcomeNewAuthor") {
            then("just send welcoming email") {
                val welcomeMsg = EmailMessages.WelcomeMsg(firstname = author.firstname)

                emailManager.welcomeNewAuthor(authorId)

                verifyOrder {
                    authorRepository.getById(authorId)
                    sendEmailVerificationOrder(welcomeMsg)
                }
            }
        }

        given("sendValidateEmail") {
            then("send validation code to email") {
                val verifyEmailMsg = EmailMessages.ValidateEmailMsg(username = author.username)

                emailManager.sendValidateEmail(authorId)

                verifyOrder {
                    authorRepository.getById(authorId)
                    sendEmailVerificationOrder(verifyEmailMsg)
                }
                coVerify {
                    appEnv.getConfig("jwt.emailSecret")
                    emailRepository.insertEmailVerificationCode(any(), authorId)
                }
            }
        }
    }
}
