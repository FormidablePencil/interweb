package integrationTests.authorization.tests

import com.jetbrains.handson.httpapi.module
import configurations.DIHelper
import domainServices.AuthorizationService
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
import shared.*

class TokensIT : BehaviorSpecIT({
    startKoin {
        modules(DIHelper.CoreModule, DITestHelper.FlowModule)
    }

    get<SignupFlow>()

    val signupFlows: SignupFlow = get()


    cleanup(true) {
        Given("created an account") {
            val result = signupFlows.signup()

            And("login") {
                // all the assertions happen in the flows
                // TokenFlow.login()

            }

            And("refresh tokens") {
                // TokenFlow.refresh()

                Then("login with new tokens given") {
                    // TokenFlow.login()

                }
            }

            And("reset password") {
                Then("login with new tokens given") {
                    // TokenFlow.login()

                }
            }
        }
    }
})

class RequestAccessTokenTest : BehaviorSpecIT() {
    private val tokenDomainService: AuthorizationService by inject()

    init {
        Given("a valid refresh token") {
            Then("return new access token and refresh token") {
                var (refreshToken, accessToken) = tokenDomainService.refreshAccessToken("refresh token")

                // region assertions
                refreshToken.length shouldBeGreaterThan 0
                accessToken.length shouldBeGreaterThan 0
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
                Then("user should get 400 error") {
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
