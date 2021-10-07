package tests.authorsPortfolio

import DTO.signup.SignupResWF
import domainServices.AuthorsPortfolioDomainService
import tests.signup.Signup_E2E

class AuthorsPortfolio_E2E(
    private val authorsPortfolioDomainService: AuthorsPortfolioDomainService,
    private val signupE2E: Signup_E2E,
) {
    fun GetLayouts() {
        //region setup
        val signupResWF: SignupResWF = signupE2E.Signup_workflow()
        //endregion

        authorsPortfolioDomainService.GetAuthorsLayouts(signupResWF.authorId)
    }

    fun GetLayout() {
        //region setup
        val signupResWF: SignupResWF = signupE2E.Signup_workflow()
        // create layout first of course
        var layoutId = 1
        //endregion

        authorsPortfolioDomainService.GetLayout(signupResWF.authorId, layoutId)
    }
}