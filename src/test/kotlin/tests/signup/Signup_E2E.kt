package tests.signup

import DTO.signup.SignupReqWF
import DTO.signup.SignupResWF
import domainServices.SignupDomainService

class Signup_E2E(
    private val signupDomainService: SignupDomainService
) {
    fun Signup_workflow(signupReqWF: SignupReqWF = SignupReqWF()): SignupResWF {
        val email = "someEmail" // randomly generate
        val username = "username" // randomly generate
        val password = "password" // randomly generate

        // wrap tranScope
        val authorId = signupDomainService.Signup(email, username, password)

        return SignupResWF(authorId, email, username)
    }
}