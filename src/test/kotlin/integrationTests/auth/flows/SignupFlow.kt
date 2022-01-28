package integrationTests.auth.flows

import dtos.signup.SignupResponse
import exceptions.ServerErrorException
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.inject
import serialized.CreateAuthorRequest
import services.AuthorizationService
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class SignupFlow : BehaviorSpecFlow() {
    private val authorizationService: AuthorizationService by inject()

    private val createAuthorRequest = AuthUtilities.createAuthorRequest

    suspend fun signup(request: CreateAuthorRequest = createAuthorRequest, cleanup: Boolean = false): SignupResponse {
        return rollback(cleanup) {
            val result = authorizationService.signup(request)

            result.statusCode() shouldBe HttpStatusCode.Created
            val data =
                result.data ?: throw ServerErrorException("Nothing in data after successful signup", this::class.java)
            data.refreshToken.length.shouldBeGreaterThan(0)
            data.accessToken.length.shouldBeGreaterThan(0)

            return@rollback result
        }
    }


}