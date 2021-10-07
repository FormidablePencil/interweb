package domainServices

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

    fun SearchAuthors(username: String) {
        authorRepository.CreateAuthor(username)
    }

    fun SearchThreadsByTitle(title: String) {

    }

    fun SearchThreadsByTags(tag: String) {
        threadRepository.GetThreadsByTag(tag)
    }

}