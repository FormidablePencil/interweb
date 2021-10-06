package domainServices

import repositories.AuthorRepository
import repositories.GroupRepository

class ExploreDomainService(
    private var authorRepository: AuthorRepository,
    private var groupRepository: GroupRepository,
) {

    fun Search(search: String) {
        SearchAuthors(search)
        SearchGroupsByTitle(search)
        SearchGroupsByTags(search)
        // return the most relevant by precedence
    }

    fun SearchAuthors(username: String) {
        authorRepository.CreateAuthor(username)
    }

    fun SearchGroupsByTitle(title: String) {

    }

    fun SearchGroupsByTags(tag: String) {
        groupRepository.GetGroupsByTag(tag)
    }

}