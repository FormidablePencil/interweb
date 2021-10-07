package domainServices

import DTOs.AuthorsPortfolioLayout
import DTOs.LayoutComponent
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
    fun GetLayout(authorId: Int, layoutId: Int): AuthorsPortfolioLayout {
        authorsPortfolioManager.GetLayout(layoutId)
        val listOfComponents = listOf(LayoutComponent(1))
        val componentArrangement = listOf(1, 2)
        return AuthorsPortfolioLayout(listOfComponents, componentArrangement)
    }

    fun GetAuthorsLayouts(authorId: Int): List<Int> {
        authorsPortfolioManager.GetAuthorsLayouts(authorId)
        return listOf<Int>(32, 43)
    }

    fun GetAuthor(userId: Int): Author? {
        return authorManager.GetAuthor(userId)
    };

    fun GetAuthor(username: String): Author? {
        return authorManager.GetAuthor(username)
    };
}