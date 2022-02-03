package integrationTests.auth.flows

import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.dtos.auth.LoginByEmailRequest
import com.idealIntent.dtos.auth.LoginByUsernameRequest
import com.idealIntent.dtos.auth.LoginResponse
import com.idealIntent.exceptions.TempException
import com.idealIntent.services.AuthorizationService
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.inject
import org.opentest4j.AssertionFailedError
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class LoginFlow : BehaviorSpecFlow() {
    private val authorizationService: AuthorizationService by inject()
    private val signupFlow: SignupFlow by inject()

    // I think I created it so to not need to got through creating an account with every integration tests since that a lot of heavy lifting
    // there are data like tokens associated with this account
    private val createAuthorRequestExistentInDb = CreateAuthorRequest(
        "6saberryyTest1235@gmail.com2", "CherryCas6as", "Alex", "Formidable!56", "Martinii"
    )

    private val loginByUsernameRequest = LoginByUsernameRequest(
        username = createAuthorRequestExistentInDb.username,
        password = createAuthorRequestExistentInDb.password,
    )
    private val loginByEmailRequest = LoginByEmailRequest(
        email = createAuthorRequestExistentInDb.email,
        password = createAuthorRequestExistentInDb.password,
    )

    private fun validateLoginResponse(result: LoginResponse) {
        result.statusCode() shouldBe HttpStatusCode.OK
        val data = result.data
            ?: throw TempException("Nothing returned after successful login", this::class.java)
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
                signupFlow.signup(createAuthorRequestExistentInDb)
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
                signupFlow.signup(createAuthorRequestExistentInDb)
            }

            val result = authorizationService.login(request)

            validateLoginResponse(result)
            return@rollback result
        }
    }
}