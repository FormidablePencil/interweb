package integrationTests.signup.flows

import dtos.author.CreateAuthorRequest
import dtos.signup.SignupResponse
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.inject
import services.AuthorizationService
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class SignupFlow : BehaviorSpecIT() {
    private val authorizationService: AuthorizationService by inject()
    private val createAuthorRequest = CreateAuthorRequest(
        "CherryCas6as", "6saberryyTest1235@gmail.com", "Alex",
        "Martini", "Formidable!56"
    )

    fun signup(request: CreateAuthorRequest = createAuthorRequest, cleanup: Boolean = false): SignupResponse {
        return rollback(cleanup) {
            val result = authorizationService.signup(request)

            result.message() shouldBe HttpStatusCode.OK
//            result.tokens.refreshToken.size shouldBeGreaterThan 0
//            result.tokens.accessToken.size shouldBeGreaterThan 0

            return@rollback result
        }.value
    }
}