package integrationTests.auth.tests

import com.auth0.jwt.JWT
import dtos.authorization.TokensResponseFailed
import dtos.responseData.ITokenResponseData
import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import kotlinx.coroutines.delay
import org.koin.test.inject
import org.opentest4j.AssertionFailedError
import serialized.CreateAuthorRequest
import serialized.auth.LoginByUsernameRequest
import services.AuthorizationService
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class TokenIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()
    val loginFlow: LoginFlow by inject()
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

                // firstly, signup/login to get the initial tokens
                // then request new refresh tokens. The initial tokens should now be invalid and replaced with the new
                val result = try {
                    loginFlow.login(LoginByUsernameRequest(signupRequest.username, signupRequest.password))
                } catch (ex: AssertionFailedError) {
                    signupFlow.signup(signupRequest)
                }

                delay(1000L) // jwt token generator for some reason needs a delay or else it'll generate same as previous one

                val InvalidRefreshToken = result.data!!.refreshToken

                val authorId = JWT().decodeJwt(InvalidRefreshToken).getClaim("authorId").asInt()

                val refreshTokenResponse = authorizationService.refreshAccessToken(InvalidRefreshToken, authorId)
                refreshTokenResponse.statusCode() shouldBe HttpStatusCode.Created
                val newRefreshToken = refreshTokenResponse.data!!.refreshToken

                val attemptWithInitialTokenAgain =
                    authorizationService.refreshAccessToken(InvalidRefreshToken, authorId)

                attemptWithInitialTokenAgain.statusCode() shouldBe HttpStatusCode.BadRequest
                attemptWithInitialTokenAgain.message() shouldBe TokensResponseFailed.getMsg(TokensResponseFailed.InvalidRefreshToken)

                val attemptWithNewToken = authorizationService.refreshAccessToken(newRefreshToken, authorId)

                attemptWithNewToken.statusCode() shouldBe HttpStatusCode.Created
            }
        }
    }

    given("reset password") {
        then("with valid password provided") {
            // todo
            // firstly signup/login to get new tokens

            // attempt to access a restricted resource with given tokens

            // then reset password which should delete all tokens

            // and try again to access a restricted resource

        }
    }

    given("sign out of all devices") {
        then("with valid password provided") {
            // todo
            // save as reset password except there's no password reset

        }
    }

    // todo
    // reset password
    // sign out of all devices (show all devices that are signed in)
})