package integrationTests.auth.tests

import dtos.authorization.LoginResponse
import exceptions.ServerErrorException
import integrationTests.auth.flows.LoginFlow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class LoginIT : BehaviorSpecIT({
    val loginFlow: LoginFlow by inject()

    fun validateLoginResponse(result: LoginResponse) {
        result.message()
        result.statusCode() shouldBe HttpStatusCode.OK

        val data = result.data
            ?: throw ServerErrorException("Nothing in data after successful login", this::class.java)
        data.refreshToken.length.shouldBeGreaterThan(0)
        data.accessToken.length.shouldBeGreaterThan(0)
    }

    Given("login") {
        Then("by email") {
            rollback {
                val result = loginFlow.loginByEmail()
                validateLoginResponse(result)
            }
        }
        Then("username") {
            rollback {
                val result = loginFlow.loginByUsername()
                validateLoginResponse(result)
            }
        }
    }
})