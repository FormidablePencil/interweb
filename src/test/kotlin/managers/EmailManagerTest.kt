package managers

import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.koin.KoinListener
import io.ktor.config.*
import io.mockk.*
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import org.koin.test.KoinTest
import repositories.codes.EmailVerificationCodeRepository
import repositories.codes.ResetPasswordCodeRepository
import repositories.profile.AuthorProfileRelatedRepository
import repositories.profile.AuthorWithDetailAndAccount
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
        val emailRepository: EmailVerificationCodeRepository = mockk(relaxed = true)
        val resetPasswordCodeRepository: ResetPasswordCodeRepository = mockk(relaxed = true)
        val authorProfileRelatedRepository: AuthorProfileRelatedRepository = mockk()
        val authorWithDetailAndAccount = AuthorWithDetailAndAccount(
            authorId = 0,
            username = "aUsername",
            firstname = "aFirstname",
            lastname = "aLastname",
            email = "someEmailAddress@gmail.com21"
        )
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

            every {
                authorProfileRelatedRepository.getAuthorWithDetailAndAccount(authorWithDetailAndAccount.authorId)
            } returns authorWithDetailAndAccount

            emailManager = spyk(
                EmailManager(emailRepository, resetPasswordCodeRepository, authorProfileRelatedRepository),
                recordPrivateCalls = true
            )

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
            eMailer.addTo(authorWithDetailAndAccount.email)
        }

        given("sendResetPasswordLink") {
            then("just send reset password link to email on file") {
                val passwordResetMsg =
                    EmailMessages.PasswordResetMsg(username = authorWithDetailAndAccount.username)

                emailManager.sendResetPasswordLink(authorWithDetailAndAccount.authorId)

                verifyOrder {
                    authorProfileRelatedRepository.getAuthorWithDetailAndAccount(authorWithDetailAndAccount.authorId)
                    sendEmailVerificationOrder(passwordResetMsg)
                }
                coVerify {
                    appEnv.getConfig("jwt.emailSecret")
                    resetPasswordCodeRepository.insert(any(), authorWithDetailAndAccount.authorId)
                }
            }
        }

        given("welcomeNewAuthor") {
            then("just send welcoming email") {
                val welcomeMsg = EmailMessages.WelcomeMsg(firstname = authorWithDetailAndAccount.firstname)

                emailManager.welcomeNewAuthor(authorWithDetailAndAccount.authorId)

                verifyOrder {
                    authorProfileRelatedRepository.getAuthorWithDetailAndAccount(authorWithDetailAndAccount.authorId)
                    sendEmailVerificationOrder(welcomeMsg)
                }
            }
        }

        given("sendValidateEmail") {
            then("send validation code to email") {
                val verifyEmailMsg = EmailMessages.ValidateEmailMsg(username = authorWithDetailAndAccount.username)

                emailManager.sendValidateEmail(authorWithDetailAndAccount.authorId)

                verifyOrder {
                    authorProfileRelatedRepository.getAuthorWithDetailAndAccount(authorWithDetailAndAccount.authorId)
                    sendEmailVerificationOrder(verifyEmailMsg)
                }
                coVerify {
                    appEnv.getConfig("jwt.emailSecret")
                    emailRepository.insert(any(), authorWithDetailAndAccount.authorId)
                }
            }
        }
    }
}
