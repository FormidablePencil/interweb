package tests.login

import DITestHelper
import configurations.DIHelper
import domainServices.SignupDomainService
import domainServices.TokenDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResWF
import dto.token.AuthenticateResponse
import dto.token.LoginResult
import models.Author
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import repositories.IAuthorRepository
import tests.signup.SignupE2E
import tests.token.TokenE2E
import java.util.*

class LoginE2E : KoinTest {
    val authorRepository by inject<IAuthorRepository>()
    val signupDomainService by inject<SignupDomainService>()
    val tokenDomainService by inject<TokenDomainService>()
    val signupE2E by inject<SignupE2E>()
    val tokenE2E by inject<TokenE2E>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(DIHelper.CoreModule, DITestHelper.CoreModule)
    }

    @Test
    fun login_test() {
        val randomNumber: Int = Random().nextInt(9999)

        var createAuthorRequest = CreateAuthorRequest(
            "username $randomNumber",
            "someEmail $randomNumber",
            "firstname",
            "lastname",
            "password",
        )


        // wrap tranScope
        signupAndLogin_flow(createAuthorRequest)

//        var loginResult = login_flow()

    }

    fun signupAndLogin_flow(request: CreateAuthorRequest): LoginResult {
        // signup and login
        signupE2E.Signup_flow(request)
        var result = tokenDomainService.login(AuthenticateResponse(request.username, request.password))

        return result
    }
}