package integrationTests.token

import com.jetbrains.handson.httpapi.module
import domainServices.SignupDomainService
import integrationTests.signup.SignupFlows
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.numerics.shouldNotBeGreaterThan
import io.kotlintest.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.test.inject
import shared.KoinBehaviorSpec

// mock dependencies
// test runner
// group tests (integration and unit tests)
// transactions

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
            val result = signupFlows.signup()

            Given("valid credentials") {
                Then("user should get tokens") {
                    loginRequest(result.username, result.password)
                    { status, content ->
                        status shouldBe HttpStatusCode.OK
                        content?.length?.shouldBeGreaterThan(0)
                    }
                }
            }

            Given("invalid credentials") {
                Then("user should get 400 error") {
                    loginRequest(result.username, "")
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