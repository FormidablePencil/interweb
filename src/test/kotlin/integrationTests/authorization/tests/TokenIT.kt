package integrationTests.authorization.tests

import dtos.token.responseData.ITokenResponseData
import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.kotest.matchers.ints.shouldBeGreaterThan
import org.koin.test.get
import org.koin.test.inject
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

    given("refresh tokens") {
        then("with valid refresh token") {
            // each device will have their own unique refresh token by adding a UUID...
            // refresh access-token -> updates the expiration only to the refresh-token that corresponds header.id with token.id
            // and updates in db and returns it to client
            // Since every device has a unique refresh-token, the devices will not have access anymore when both tokens expire
            // and when refreshing access-token, all the other tokens are not updated, the refresh-token in db with id corresponding
            // with the provided valid refresh-token id value which we put in the beginning for this purpose

            // todo
            // first signup, then access restricted data with tokens given
            // then request refresh tokens and try to access restricted data with old tokens and new tokens given

            rollback {
                val res = authorizationService.refreshAccessToken("refresh token", 1)

                validateThatTokensReturned(res.data!!)
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