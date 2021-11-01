package integrationTests.authorization.tests

import com.jetbrains.handson.httpapi.module
import configurations.DIHelper
import dtos.author.CreateAuthorRequest
import integrationTests.signup.flows.SignupFlow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldNotBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.test.get
import org.koin.test.inject
import services.AuthorizationService
import shared.BehaviorSpecIT
import shared.DITestHelper
import shared.rollbackGiven

class TokensIT : BehaviorSpecIT({
    startKoin {
        modules(DIHelper.CoreModule, DITestHelper.FlowModule)
    }

    get<SignupFlow>()

    val signupFlows: SignupFlow = get()


    rollbackGiven("created an account") {
        val result = signupFlows.signup()

        And("login") {
            // all the assertions happen in the flows
            // TokenFlow.login()

        }

        And("refresh tokens") {
            // each device will have their own unique refresh token by adding a UUID...
            // refresh access-token -> updates the expiration only to the refresh-token that corresponds header.id with token.id
            // and updates in db and returns it to client
            // Since every device has a unique refresh-token, the devices will not have access anymore when both tokens expire
            // and when refreshing access-token, all the other tokens are not updated, the refresh-token in db with id corresponding
            // with the provided valid refresh-token id value which we put in the beginning for this purpose

            // TokenFlow.refresh()

            Then("login with new tokens given") {
                // TokenFlow.login()

            }
        }

        And("reset password") {
            Then("login with new tokens given") {
                // TokenFlow.login()

            }

            Then("login with old tokens given") {
                // TokenFlow.login()

            }
        }
    }
})

class RequestAccessTokenTest : BehaviorSpecIT() {
    private val tokenDomainService: AuthorizationService by inject()

    init {
        Given("a valid refresh token") {
            Then("return new access token and refresh token") {
//                var (refreshToken, accessToken)
                val result = tokenDomainService.refreshAccessToken("refresh token", 1)

                // region assertions
                val data = result.data ?: throw Exception("test failed")
                data.refreshToken.length shouldBeGreaterThan 0
                data.accessToken.length shouldBeGreaterThan 0
                // endregion
            }
        }
    }
}

class GetTokensUponLogin : BehaviorSpecIT() {
    private val signupFlows = SignupFlow()

    private fun loginRequest(
        email: String, password: String,
        assertion: (status: HttpStatusCode?, content: String?) -> Unit
    ) {
        withTestApplication({ module(testing = true) }) {
            with(handleRequest(HttpMethod.Post, "/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(
                    listOf(
                        "email" to email,
                        "password" to password,
                    ).formUrlEncode()
                )
            }) {
                assertion(response.status(), response.content)
            }
        }
    }

    init {
        // create a wrapper to catch cleanup exceptions
        try {
            // first create an account via domainServices
            val createAuthorRequest = CreateAuthorRequest(
                "username", "email", "firstname",
                "lastname", "password"
            )

            val result = signupFlows.signup()

            Given("valid credentials") {
                Then("user should get tokens") {
                    loginRequest(createAuthorRequest.username, createAuthorRequest.password)
                    { status, content ->
                        status shouldBe HttpStatusCode.OK
                        content?.length?.shouldBeGreaterThan(0)
                    }
                }
            }

            Given("invalid credentials") {
                Then("user should get 400 code") {
                    loginRequest(createAuthorRequest.username, "")
                    { status, content ->
                        status shouldBe HttpStatusCode.BadRequest
                        content?.length?.shouldNotBeGreaterThan(0)
                    }
                }
            }

        } catch (ex: Exception) {
            if (ex.message == "cleanup")
            else throw Exception(ex)
        }
    }
}
