package integrationTests.authorsPortfolio

import domainServices.AuthorsPortfolioDomainService
import domainServices.SignupDomainService
import domainServices.TokenDomainService
import integrationTests.login.LoginFlows
import org.junit.Test
import integrationTests.signup.SignupFlows
import org.koin.test.inject

class AuthorsPortfolioFlows(
) {
    fun GetLayouts() {
        //region setup
//        val signupResWF: SignupResWF = signupE2E.Signup_flow()
        //endregion

//        authorsPortfolioDomainService.GetAuthorsLayouts(signupResWF.authorId)
    }

    fun GetLayout() {
        //region setup
//        val signupResWF: SignupResWF = signupE2E.Signup_flow()
        // create layout first of course
        var layoutId = 1
        //endregion

//        authorsPortfolioDomainService.GetLayout(signupResWF.authorId, layoutId)
    }

    //region experimentation
    @Test
    fun Exper() {
//            signupDomainService.Signup("myEmail@gmail.com", "Illustrious-Elk-Love", "password")
//            authorsPortfolioDomainService.GetAuthorByEmail("Illustrious-Elk-Love")
    }
    //endregion
}