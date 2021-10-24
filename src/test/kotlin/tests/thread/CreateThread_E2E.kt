package tests.thread

import dto.signup.SignupResWF
import dto.thread.CreateThreadReqWF
import domainServices.ExploreDomainService
import domainServices.ThreadDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import tests.signup.SignupE2E

class CreateThread_E2E(
    private val threadDomainService: ThreadDomainService,
    private val exploreDomainService: ExploreDomainService,
    private val signupE2E: SignupE2E,
) {

    fun CreateThread_workflow(createThreadRequest: CreateThreadReqWF = CreateThreadReqWF()) {
        // surround code with a transaction scope
        var createAuthorRequest = CreateAuthorRequest("", "", "", "", "")
        val signupResult: SignupResult = signupE2E.Signup_flow(createAuthorRequest)

        threadDomainService.CreateThread(signupResult.authorId)

        // validate that it was created through another user

//        if (createThreadRequest.cleanup)
//            transactionScope.Complete()
    }
}