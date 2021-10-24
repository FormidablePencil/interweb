package integrationTests.login

import domainServices.TokenDomainService
import dto.author.CreateAuthorRequest
import dto.token.AuthenticateResponse
import dto.token.LoginResult
import integrationTests.signup.SignupFlows
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*

class LoginFlows : KoinTest {
    private val tokenDomainService by inject<TokenDomainService>()
    private val signupFlows = SignupFlows()

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
        signupAndLogin(createAuthorRequest)

//        var loginResult = login_flow()

    }

    fun signupAndLogin(request: CreateAuthorRequest): LoginResult {
        // signup and login
        signupFlows.Signup_flow(request)
        var result = tokenDomainService.login(AuthenticateResponse(request.username, request.password))

        return result
    }
}