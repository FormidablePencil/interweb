package integrationTests

import com.idealIntent.module
import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.core.context.stopKoin
import org.koin.test.inject
import org.opentest4j.AssertionFailedError
import serialized.CreateAuthorRequest
import serialized.LoginByUsernameRequest
import shared.testUtils.BehaviorSpecIT
import kotlin.test.assertEquals

class ApiRequestIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()
    val loginFlow: LoginFlow by inject()

    given("get order") {
        then("with valid token, return order") {
            val createAuthorRequest = CreateAuthorRequest(
                "someEmail@gmail.com123Hello", "Billy", "Bob", "Unforgettable!123", "EatIt"
            )

            // first signup/login to get the tokens
            val result = try {
                loginFlow.login(LoginByUsernameRequest(createAuthorRequest.username, createAuthorRequest.password))
            } catch (ex: AssertionFailedError) {
                signupFlow.signup(createAuthorRequest)
            }

            val tokens = result.data!!
            stopKoin()

            withTestApplication({ module(testing = true) }) {
                val res = handleRequest(HttpMethod.Get, "/order/2020-04-06-01") {
                    addHeader(HttpHeaders.Authorization, "Bearer ${tokens.accessToken}")
                }
                assertEquals(
                    """{"number":"2020-04-06-01","contents":[{"item":"Ham Sandwich","amount":2,"price":5.5},{"item":"Water","amount":1,"price":1.5},{"item":"Beer","amount":3,"price":2.3},{"item":"Cheesecake","amount":1,"price":3.75}]}""",
                    res.response.content
                )
                println(res.response.status())
                println(res.response.content)
            }
        }
    }
})

//