package managers

import models.Author
import repositories.AuthorRepository
import repositories.AuthorViewerRepository

class AuthorManager(
    token: String,
    private val authorRepository: AuthorRepository,
    private val authorViewerRepository: AuthorViewerRepository,
) : OwnerOrViewManager(token), IAuthorManager {

    override fun GetAuthorByUsername(username: String): Author? {
        return if (requesterUsername == username)
            authorRepository.GetByUsername(username)
        else
            authorViewerRepository.Get(username)
    }

    override fun GetAuthorById(userId: Int): Author? {
        return if (requesterId == userId)
            authorRepository.GetById(userId)
        else
            authorViewerRepository.Get(userId)
    };
}