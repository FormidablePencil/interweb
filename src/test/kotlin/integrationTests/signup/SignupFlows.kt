package integrationTests.signup

import domainServices.SignupDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupFlowResultWF
import dto.signup.SignupResult
import helper.DbHelper
import org.koin.test.KoinTest
import org.koin.test.inject

class SignupFlows : KoinTest {
    private val signupDomainService: SignupDomainService by inject()
    private val dbHelper: DbHelper by inject()

    fun signup(request: CreateAuthorRequest, cleanup: Boolean = false): SignupResult {
        dbHelper.database.useTransaction {
            val result = signupDomainService.signup(request)

//        return SignupResWF(signupResponse.authorId)

            if (cleanup) throw Exception("cleanup")
        }
        return signupDomainService.signup(request)
    }

    fun signup(cleanup: Boolean = false): SignupFlowResultWF {
        val createAuthorRequest = CreateAuthorRequest(
            "username", "email", "firstname",
            "lastname", "password"
        )
        dbHelper.database.useTransaction {
            val signupResult = signupDomainService.signup(createAuthorRequest)

            if (cleanup) throw Exception("cleanup")

            return SignupFlowResultWF(
                createAuthorRequest.username, createAuthorRequest.email,
                createAuthorRequest.password, signupResult.authorId
            )
        }
    }
}