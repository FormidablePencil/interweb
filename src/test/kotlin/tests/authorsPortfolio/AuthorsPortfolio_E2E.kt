package tests.authorsPortfolio

import dto.signup.SignupResWF
import domainServices.AuthorsPortfolioDomainService
import domainServices.SignupDomainService
import org.junit.Test
import tests.signup.Signup_E2E

class AuthorsPortfolio_E2E(
    private val authorsPortfolioDomainService: AuthorsPortfolioDomainService,
    private val signupDomainService: SignupDomainService,
    private val signupE2E: Signup_E2E,
) {
    fun GetLayouts() {
        //region setup
        val signupResWF: SignupResWF = signupE2E.Signup_flow()
        //endregion

        authorsPortfolioDomainService.GetAuthorsLayouts(signupResWF.authorId)
    }

    fun GetLayout() {
        //region setup
        val signupResWF: SignupResWF = signupE2E.Signup_flow()
        // create layout first of course
        var layoutId = 1
        //endregion

        authorsPortfolioDomainService.GetLayout(signupResWF.authorId, layoutId)
    }

    //region experimentation
    @Test
    fun Exper() {
//            signupDomainService.Signup("myEmail@gmail.com", "Illustrious-Elk-Love", "password")
//            authorsPortfolioDomainService.GetAuthorByUsername("Illustrious-Elk-Love")
    }
    //endregion
}