package managers

import models.Author
import repositories.AuthorRepository
import repositories.AuthorViewerRepository

class AuthorManager(
    token: String,
    private val authorRepository: AuthorRepository,
    private val authorViewerRepository: AuthorViewerRepository,
) : OwnerOrViewManager(token), IAuthorManager {

    override fun GetAuthor(username: String): Author? {
        return if (requesterUsername == username)
            authorRepository.Get(username)
        else
            authorViewerRepository.Get(username)
    }

    override fun GetAuthor(userId: Int): Author? {
        return if (requesterId == userId)
            authorRepository.Get(userId)
        else
            authorViewerRepository.Get(userId)
    };
}