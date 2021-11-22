package integrationTests.authorization.flows

import shared.testUtils.BehaviorSpecIT

// directories
// integration tests.
// unit tests (mock generators).
// shared - flows for integration tests, di

class TokenFlow : BehaviorSpecIT() {
    val loginFlow = LoginFlowDeprecated()

    // Tests
    // refresh token fails if expired
    // refresh token fails if not the same as in db

    // access token fails if expired
    // access token fails if not the same as in db

    // store refresh tokens and access tokens in db

    // There should be no sensitive information in jwt tokens
    // TP has is where you send the encrypted password in the token.
    // The server tries to decrypt the token with the corresponding data, UserId and encrypted password in their case
    // and if success then give access to restricted requested resources.


    suspend fun login() {
        var result = loginFlow.signupAndLogin()

//        result.tokens.RefreshToken.length shouldNotBe 0
        // expiration date greater than something and less than another thing
        // attempt to access data with tempered token and attempt to access another users resources
    }

    suspend fun expiredRefreshToken() {
        // we can make http calls and dependency inject mocked version of dependencies to test the controllers

        //region setup

        var result = loginFlow.signupAndLogin()
        // send credentials generate refresh token with 1 second expiration time
        // delay runtime and validate refresh token
        //endregion

        // should fail
    }

    fun expiredAccessToken() {
        // practically the same as ExpiredRefreshToken
    }
}

// convert to working format
//class LoginTests : KoinFunSpec() {
//    private val loginFlow by inject<LoginFlowDeprecated>()
//
//    init {
//        // about the tokens, verification of correction should be done by unit tests and mocks
//        test("login") {
//            //region setup
//            val randomNumber: Int = Random().nextInt(9999)
//
//            var createAuthorRequest = CreateAuthorRequest(
//                "email $randomNumber",
//                "someEmail $randomNumber",
//                "firstname",
//                "lastname",
//                "password",
//            )
//            //endregion
//
//            //region action
//            // wrap tranScope
//            var result = loginFlow.signupAndLogin(createAuthorRequest)
//            //endregion
//
//            //region assertions
//            result.authorId shouldNotBe  null
//            //region
//        }
//    }
//}
