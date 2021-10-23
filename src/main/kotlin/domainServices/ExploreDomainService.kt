package domainServices

import models.Author
import models.Thread
import repositories.AuthorRepository
import repositories.ThreadRepository

class ExploreDomainService(
    private var authorRepository: AuthorRepository,
    private var threadRepository: ThreadRepository,
) {

    fun Search(search: String) {
        SearchAuthors(search)
        SearchThreadsByTitle(search)
        SearchThreadsByTags(search)
        // return the most relevant by precedence
    }

    fun SearchAuthors(username: String): Author {
        val author = authorRepository.GetByUsername(username)
        if (author != null)
            return author
        else throw Exception("failed to find author...")
    }

    fun SearchThreadsByTitle(title: String): List<Thread> {
        return listOf<Thread>(Thread())
    }

    fun SearchThreadsByTags(tag: String) {
        threadRepository.GetThreadsByTag(tag)
    }

}