package integrationTests.login

import domainServices.LoginDomainService
import dto.author.CreateAuthorRequest
import dto.token.LoginResult
import integrationTests.signup.SignupFlows
import org.koin.test.KoinTest
import org.koin.test.inject

class LoginFlows : KoinTest {
    private val loginDomainService by inject<LoginDomainService>()
    private val signupFlows = SignupFlows()

    fun signupAndLogin(request: CreateAuthorRequest): LoginResult {
        // signup and login
        signupFlows.signup(request)
        var result = loginDomainService.login(request.username, "")

        return result
    }
}