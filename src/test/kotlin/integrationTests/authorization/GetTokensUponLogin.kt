package integrationTests.authorization

import com.jetbrains.handson.httpapi.module
import dto.author.CreateAuthorRequest
import integrationTests.signup.SignupFlows
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.numerics.shouldNotBeGreaterThan
import io.kotlintest.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import shared.KoinBehaviorSpec

// test runner
// group tests (integration and unit tests)

class GetTokensUponLogin : KoinBehaviorSpec() {
    private val signupFlows = SignupFlows()

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