package domainServices

import managers.AuthorManager
import managers.AuthorsPortfolioManager
import models.Author
import models.AuthorsPortfolio.AddNewComponentRequest
import repositories.AuthorRepository
import repositories.AuthorViewerRepository


class AuthorsPortfolioDomainService(
    private var authorManager: AuthorManager,
    private val authorsPortfolioManager: AuthorsPortfolioManager,
) {
    fun GetLayout(layoutId: Int) {
        authorsPortfolioManager.GetLayout(layoutId)
    }

    fun GetAuthorsLayouts() {
        authorsPortfolioManager.GetAuthorsLayouts()
    }


    fun GetAuthor(userId: Int): Author? {
        return authorManager.GetAuthor(userId)
    };

    fun GetAuthor(username: String): Author? {
        return authorManager.GetAuthor(username)
    };
}