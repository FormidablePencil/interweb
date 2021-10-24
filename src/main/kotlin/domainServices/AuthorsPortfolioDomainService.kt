package domainServices

import dto.AuthorsPortfolioLayout
import dto.LayoutComponent
import managers.AuthorsPortfolioManager
import models.Author
import repositories.AuthorRepository
import repositories.IAuthorRepository


class AuthorsPortfolioDomainService(
    private val authorRepository: IAuthorRepository,
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

    fun GetAuthorById(userId: Int): Author? {
        return authorRepository.getById(userId)
    };

    fun GetAuthorByEmail(email: String): Author? {
        return authorRepository.getByEmail(email)
    };
}