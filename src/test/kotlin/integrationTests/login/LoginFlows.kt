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

    fun signupAndLogin(): LoginResult {
        // signup and login
        val createAuthorRequest = CreateAuthorRequest(
            "Formidable@78",
            "someEmail@gmail.com",
            "firstname",
            "lastname",
            "password"
        )
        val signupResult = signupFlows.signup()
        val result = loginDomainService.login(createAuthorRequest.username, createAuthorRequest.password)

        return result
    }
}