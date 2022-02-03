package integrationTests.auth.flows

import com.auth0.jwt.JWT
import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.dtos.auth.SignupResponse
import com.idealIntent.exceptions.TempException
import com.idealIntent.services.AuthorizationService
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.inject
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
                result.data ?: throw TempException("Nothing in data after successful signup", this::class.java)
            data.refreshToken.length.shouldBeGreaterThan(0)
            data.accessToken.length.shouldBeGreaterThan(0)

            return@rollback result
        }
    }

    /**
     * Create account and return author id
     *
     * @return Id of newly created author
     */
    suspend fun signupReturnId(request: CreateAuthorRequest = createAuthorRequest, cleanup: Boolean = false): Int {
        val accessToken = signup(request, cleanup).data?.accessToken
        return JWT().decodeJwt(accessToken).getClaim("authorId").asInt()
    }
}