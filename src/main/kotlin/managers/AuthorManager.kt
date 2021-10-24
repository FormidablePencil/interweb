package managers

import models.Author
import repositories.AuthorRepository
import repositories.AuthorViewerRepository

class AuthorManager(
    token: String,
    private val authorRepository: AuthorRepository,
    private val authorViewerRepository: AuthorViewerRepository,
) : OwnerOrViewManager(token), IAuthorManager {

    override fun GetAuthorByEmail(email: String): Author? {
        return if (requesterUsername == email)
            authorRepository.getByEmail(email)
        else
            authorViewerRepository.Get(email)
    }

    override fun GetAuthorById(userId: Int): Author? {
        return if (requesterId == userId)
            authorRepository.getById(userId)
        else
            authorViewerRepository.Get(userId)
    };
}