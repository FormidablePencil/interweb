package integrationTests.auth.flows

import dtos.authorization.LoginResponse
import exceptions.ServerErrorException
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.inject
import org.opentest4j.AssertionFailedError
import serialized.CreateAuthorRequest
import serialized.LoginByEmailRequest
import serialized.LoginByUsernameRequest
import services.AuthorizationService
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class LoginFlow : BehaviorSpecFlow() {
    private val authorizationService: AuthorizationService by inject()
    private val signupFlow: SignupFlow by inject()

    private val createAuthorRequest = CreateAuthorRequest(
        "6saberryyTest1235@gmail.com", "CherryCas6as", "Alex", "Formidable!56", "Martini"
    )
    private val loginByUsernameRequest = LoginByUsernameRequest(
        username = createAuthorRequest.username,
        password = createAuthorRequest.password,
    )
    private val loginByEmailRequest = LoginByEmailRequest(
        email = createAuthorRequest.email,
        password = createAuthorRequest.password,
    )

    private fun validateLoginResponse(result: LoginResponse) {
        result.statusCode() shouldBe HttpStatusCode.OK
        val data = result.data
            ?: throw ServerErrorException("Nothing returned after successful login", this::class.java)
        data.refreshToken.length.shouldBeGreaterThan(0)
        data.accessToken.length.shouldBeGreaterThan(0)
    }

    suspend fun login(request: LoginByUsernameRequest): LoginResponse {
        return loginByUsername(request)
    }

    suspend fun loginByUsername(
        request: LoginByUsernameRequest = loginByUsernameRequest,
        cleanup: Boolean = false
    ): LoginResponse {
        return rollback(cleanup) {
            // attempt to log in first before creating an account
            try {
                val loginResult = authorizationService.login(request)
                validateLoginResponse(loginResult)
            } catch (ex: AssertionFailedError) {
                signupFlow.signup(createAuthorRequest)
            }

            val result = authorizationService.login(request)

            validateLoginResponse(result)
            return@rollback result
        }
    }

    suspend fun loginByEmail(
        request: LoginByEmailRequest = loginByEmailRequest,
        cleanup: Boolean = false
    ): LoginResponse {
        return rollback(cleanup) {
            try {
                // attempt to log in first before creating an account
                val loginResult = authorizationService.login(request)
                validateLoginResponse(loginResult)
            } catch (ex: AssertionFailedError) {
                signupFlow.signup(createAuthorRequest)
            }

            val result = authorizationService.login(request)

            validateLoginResponse(result)
            return@rollback result
        }
    }
}