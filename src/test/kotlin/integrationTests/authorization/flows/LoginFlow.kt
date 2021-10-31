package integrationTests.authorization.flows

import services.AuthorizationService
import dtos.author.CreateAuthorRequest
import dtos.authorization.LoginResponse
import dtos.login.LoginByEmailRequest
import integrationTests.signup.flows.SignupFlow
import org.koin.test.KoinTest
import org.koin.test.inject

class LoginFlow : KoinTest {
    private val authorizationDomainService by inject<AuthorizationService>()
    private val signupFlows = SignupFlow()

    fun signupAndLogin(): LoginResponse {
        // signup and login
        val createAuthorRequest = CreateAuthorRequest(
            "Formidable@78",
            "someEmail@gmail.com",
            "firstname",
            "lastname",
            "password"
        )
        val signupResult = signupFlows.signup()
        val result = authorizationDomainService.login(
            LoginByEmailRequest(email = createAuthorRequest.email, password = createAuthorRequest.password)
        )

        return result
    }
}
