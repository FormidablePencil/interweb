package integrationTests.authorization.tests

import com.auth0.jwt.JWT
import dtos.authorization.TokensResponseFailed
import dtos.token.responseData.ITokenResponseData
import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.koin.test.get
import org.koin.test.inject
import org.opentest4j.AssertionFailedError
import serialized.CreateAuthorRequest
import serialized.LoginByUsernameRequest
import services.AuthorizationService
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class TokenIT : BehaviorSpecIT({
    val signupFlow: SignupFlow = get()
    val loginFlow: LoginFlow = get()
    val authorizationService: AuthorizationService by inject()

    fun validateThatTokensReturned(data: ITokenResponseData) {
        data.refreshToken.length shouldBeGreaterThan 0
        data.accessToken.length shouldBeGreaterThan 0
    }

    given("signup") {
        then("with valid credentials") {
            rollback {
                val res = signupFlow.signup()

                validateThatTokensReturned(res.data!!)
            }
        }
    }

    given("login") {
        then("with valid credentials") {
            rollback {
                val res = loginFlow.loginByUsername()

                validateThatTokensReturned(res.data!!)
            }
        }
    }

    given("an initial refresh token") {
        When("requested for new tokens") {
            Then("initial refresh token should be denied and the new token should be accepted the next request for new tokens") {
                val signupRequest = CreateAuthorRequest(
                    "someEmail@gmail.com123Hello", "Billy", "Bob", "Unforgettable!123", "EatIt"
                )

                // first signup/login to get the initial tokens
                // request new refresh tokens which should make initial tokens not valid anymore
                val result = try {
                    loginFlow.login(LoginByUsernameRequest(signupRequest.username, signupRequest.password))
                } catch (ex: AssertionFailedError) {
                    signupFlow.signup(signupRequest)
                }

                val initialAccessToken = result.data!!.accessToken

                val authorId = JWT().decodeJwt(initialAccessToken).getClaim("authorId").asInt()

                val refreshTokenResponse = authorizationService.refreshAccessToken(initialAccessToken, authorId)
                val newAccessToken = refreshTokenResponse.data!!.accessToken

                val attemptWithInitialTokenAgain = authorizationService.refreshAccessToken(initialAccessToken, authorId)

                attemptWithInitialTokenAgain.statusCode() shouldBe HttpStatusCode.BadRequest
                attemptWithInitialTokenAgain.message() shouldBe TokensResponseFailed.getMsg(TokensResponseFailed.InvalidRefreshToken)

                val attemptWithNewToken = authorizationService.refreshAccessToken(newAccessToken, authorId)

                attemptWithNewToken.statusCode() shouldBe HttpStatusCode.Created
            }
        }
    }

    given("reset password") {
        then("with valid password provided") {
            // todo
            // first signup, then access restricted data with tokens given
            // then reset password and try to access restricted data with old tokens and new tokens given
        }
    }

    given("requested sign out in all devices") {
        then("with valid password provided") {

        }
    }

    xgiven("sign out specific device") {
        then("with valid password provided") {
            // todo - create a sign from specified devices
        }
    }
})