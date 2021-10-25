package integrationTests.login

import dto.author.CreateAuthorRequest
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.koin.test.inject
import shared.KoinFunSpec
import java.util.*

// convert to working format
class LoginTests : KoinFunSpec() {
    private val loginFlows by inject<LoginFlows>()

    init {
        // about the tokens, verification of correction should be done by unit tests and mocks
        test("login") {
            //region setup
            val randomNumber: Int = Random().nextInt(9999)

            var createAuthorRequest = CreateAuthorRequest(
                "email $randomNumber",
                "someEmail $randomNumber",
                "firstname",
                "lastname",
                "password",
            )
            //endregion

            //region action
            // wrap tranScope
            var result = loginFlows.signupAndLogin(createAuthorRequest)
            //endregion

            //region assertions
            result.authorId shouldNotBe  null
            //region
        }
    }
}