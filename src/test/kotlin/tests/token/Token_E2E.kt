package tests.token

import DITestHelper
import configurations.DIHelper
import dto.author.CreateAuthorRequest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import tests.login.LoginE2E

class TokenE2E(): KoinTest {
    val login_E2E by inject<LoginE2E>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(DIHelper.CoreModule, DITestHelper.CoreModule)
    }

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

    @Test
    fun loginTokens() {
        var createAuthorRequest = CreateAuthorRequest(
            "Formidable@78",
            "someEmail@gmail.com",
            "firstname",
            "lastname",
            "PurpleBlue@54",
        )

        var result = login_E2E.signupAndLogin_flow(createAuthorRequest)

        Assert.assertTrue(result.refreshToken.isNotEmpty())
        // expiration date greater than something and less than another thing
        // attempt to access data with tempered token and attempt to access another users resources
    }

    fun expiredRefreshToken() {
        // we can make http calls and dependency inject mocked version of dependencies to test the controllers

        //region setup
        var createAuthorRequest = CreateAuthorRequest(
            "Formidable@78",
            "someEmail@gmail.com",
            "firstname",
            "lastname",
            "PurpleBlue@54",
        )
        var result = login_E2E.signupAndLogin_flow(createAuthorRequest)
        // send credentials generate refresh token with 1 second expiration time
        // delay runtime and validate refresh token
        //endregion

        // should fail
    }

    fun expiredAccessToken() {
        // practically the same as ExpiredRefreshToken
    }
}