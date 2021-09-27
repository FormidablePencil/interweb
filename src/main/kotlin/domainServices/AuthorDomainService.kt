package domainServices

import managers.AuthorManager
import models.Author
import repositories.AuthorRepository
import repositories.AuthorViewerRepository


class AuthorDomainService(
    private var authorManager: AuthorManager,
) {

    fun GetAuthor(userId: Int): Author? {
        return authorManager.GetAuthor(userId)
    };

    fun GetAuthor(username: String): Author? {
        return authorManager.GetAuthor(username)
    };
}