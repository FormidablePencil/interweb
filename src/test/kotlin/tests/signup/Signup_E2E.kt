package tests.signup

import dto.signup.SignupReqWF
import dto.signup.SignupResWF
import domainServices.SignupDomainService
import org.koin.test.KoinTest
import org.koin.test.inject
import repositories.IAuthorRepository

class Signup_E2E : KoinTest {
    val signupDomainService by inject<SignupDomainService>()
    val authorRepository by inject<IAuthorRepository>()

//    @get:Rule
//    val koinTestRule = KoinTestRule.create {
//        modules(DIHelper.GetModule(environment.config))
//    }

    fun Signup_flow(signupReqWF: SignupReqWF = SignupReqWF()): SignupResWF {
        val email = "someEmail" // randomly generate
        val username = "username" // randomly generate
        val password = "password" // randomly generate

        // wrap tranScope
        val authorId = signupDomainService.Signup(email, username, password)

        return SignupResWF(authorId, email, username)
    }
}