package integrationTests.signup

import domainServices.SignupDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import org.koin.test.KoinTest
import org.koin.test.inject
import repositories.IAuthorRepository

class SignupFlows : KoinTest {
    val signupDomainService by inject<SignupDomainService>()
    val authorRepository by inject<IAuthorRepository>()

    fun Signup_flow(request: CreateAuthorRequest): SignupResult {
        // wrap tranScope
        return signupDomainService.Signup(request)

//        return SignupResWF(signupResponse.authorId)
    }
}