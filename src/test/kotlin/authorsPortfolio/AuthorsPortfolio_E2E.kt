package authorsPortfolio

import domainServices.AuthorsPortfolioDomainService

class AuthorsPortfolio_E2E(
    private val authorsPortfolioDomainService: AuthorsPortfolioDomainService,
) {
    fun GetLayouts() {
        authorsPortfolioDomainService.GetAuthorsLayouts()
    }

    fun GetLayout() {
        //region setup

        // create layout first ofcourse
        var layoutId = 1

        //endregion

        authorsPortfolioDomainService.GetLayout(layoutId)
    }

}