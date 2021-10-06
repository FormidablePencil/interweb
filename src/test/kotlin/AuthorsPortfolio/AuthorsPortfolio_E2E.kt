package AuthorsPortfolio

import managers.AuthorsPortfolioManager

class AuthorsPortfolio_E2E(
    private val authorsPortfolioManager: AuthorsPortfolioManager,
) {
    fun GetLayouts() {
        authorsPortfolioManager.GetLayouts()
    }

}