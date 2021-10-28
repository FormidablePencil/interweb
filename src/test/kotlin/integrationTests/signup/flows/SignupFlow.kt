package integrationTests.signup.flows

import services.SignupService
import dtos.author.CreateAuthorRequest
import dtos.signup.SignupResult
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.BehaviorSpecIT
import shared.cleanup

class SignupFlow : BehaviorSpecIT() {
    private val signupDomainService: SignupService by inject()
    private val createAuthorRequest = CreateAuthorRequest(
        "CherryCas6as", "6saberryyTest1235@gmail.com", "Alex",
        "Martini", "Formidable!56"
    )

    fun signup(request: CreateAuthorRequest = createAuthorRequest, cleanup: Boolean = false): SignupResult {
        return cleanup(cleanup) {
            val result = signupDomainService.signup(request)

            result.authorId shouldNotBe null
//            result.tokens.refreshToken.size shouldBeGreaterThan 0
//            result.tokens.accessToken.size shouldBeGreaterThan 0

            return@cleanup result
        }.value
    }
}