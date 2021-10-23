package tests.login

import dto.signup.SignupResWF
import domainServices.SignupDomainService
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import repositories.IAuthorRepository
import tests.signup.Signup_E2E
import tests.token.Token_E2E

class Login_E2E : KoinTest {
    val authorRepository by inject<IAuthorRepository>()
    val signupDomainService by inject<SignupDomainService>()
    val signup_E2E by inject<Signup_E2E>()
    val token_E2E by inject<Token_E2E>()

//    @get:Rule
//    val koinTestRule = KoinTestRule.create {
//        modules(DIHelper.CoreModule)
//    }

    @Test
    fun Login_test() {
        val email = "someEmail" // randomly generate
        val username = "username" // randomly generate
        val password = "password" // randomly generate

        // wrap tranScope
        val authorId = signupDomainService.Signup(email, username, password)

        var signupResult: SignupResWF = signup_E2E.Signup_flow();

        var loginResult = Login_flow()

    }

    fun Login_flow() {
        token_E2E.Authenticate()
    }
}