package tests.thread

import dto.signup.SignupResWF
import dto.thread.CreateThreadReqWF
import domainServices.ExploreDomainService
import domainServices.ThreadDomainService
import tests.signup.Signup_E2E

class CreateThread_E2E(
    private val threadDomainService: ThreadDomainService,
    private val exploreDomainService: ExploreDomainService,
    private val signupE2E: Signup_E2E,
) {

    fun CreateThread_workflow(createThreadRequest: CreateThreadReqWF = CreateThreadReqWF()) {
        // surround code with a transaction scope
        val signupResWF: SignupResWF = signupE2E.Signup_flow()

        threadDomainService.CreateThread(signupResWF.authorId)

        // validate that it was created through another user

//        if (createThreadRequest.cleanup)
//            transactionScope.Complete()
    }
}