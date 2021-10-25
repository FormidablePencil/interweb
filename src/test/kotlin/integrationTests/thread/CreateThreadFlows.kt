package integrationTests.thread

import dto.thread.CreateThreadReqWF
import domainServices.ExploreDomainService
import domainServices.SignupDomainService
import domainServices.ThreadDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import integrationTests.signup.SignupFlows
import org.koin.test.KoinTest
import org.koin.test.inject
import shared.KoinFunSpec

class CreateThreadFlows: KoinTest {
    private val threadDomainService by inject<ThreadDomainService>()
    private val exploreDomainService by inject<ExploreDomainService>()
    private val signupDomainService by inject<SignupDomainService>()
    private val signupFlows = SignupFlows()

    fun CreateThread(createThreadRequest: CreateThreadReqWF = CreateThreadReqWF()) {
        // surround code with a transaction scope
        var createAuthorRequest = CreateAuthorRequest("", "", "", "", "")
        val signupResult: SignupResult = signupFlows.Signup_flow(createAuthorRequest)

        threadDomainService.createThread(signupResult.authorId)

        // validate that it was created through another user

//        if (createThreadRequest.cleanup)
//            transactionScope.Complete()
    }
}