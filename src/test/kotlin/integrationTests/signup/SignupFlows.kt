package integrationTests.signup

import domainServices.SignupDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.KoinBehaviorSpec
import shared.cleanup

class SignupFlows : KoinBehaviorSpec() {
    private val signupDomainService: SignupDomainService by inject()
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