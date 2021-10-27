package integrationTests.signup

import domainServices.SignupDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldNotBe
import org.koin.test.inject
import shared.KoinBehaviorSpec

class SignupFlows : KoinBehaviorSpec() {
    private val signupDomainService: SignupDomainService by inject()
    private val createAuthorRequest = CreateAuthorRequest(
        "username", "email", "firstname",
        "lastname", "password"
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