package integrationTests.token

import com.jetbrains.handson.httpapi.module
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.ktor.http.*
import io.ktor.server.testing.*

// mock dependencies
// test runner
// group tests (integration and unit tests)
// transactions

class GetTokensUponLogin : BehaviorSpec() {
    private fun loginRequest(email: String, password: String, assertion: (status: HttpStatusCode?) -> Unit) {
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
                assertion(response.status())
            }
        }
    }

    init {
        // first create an account via domainServices

        Given("valid credentials") {
            Then("user should get tokens") {
                loginRequest("email", "Password") { status -> status shouldBe HttpStatusCode.OK }
            }
        }

        Given("invalid credentials") {
            Then("user should get 400 error") {
                loginRequest("email", "Password") { status -> status shouldBe HttpStatusCode.BadRequest }
            }
        }

        // after we are done with test, cleanup with transaction not completing
    }
}