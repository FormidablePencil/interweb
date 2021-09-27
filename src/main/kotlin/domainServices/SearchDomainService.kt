package domainServices

import repositories.AuthorRepository
import repositories.GroupRepository

class SearchDomainService(
    private var authorRepository: AuthorRepository,
    private var groupRepository: GroupRepository,
    private var groupCategoriesRepository: CategoriesOfGroupRepository
) {

    fun Search(search: String) {
        SearchAuthors(search)
        SearchGroupsByTitle(search)
        SearchGroupsByTags(search)
        // return the most relevant by precedence
    }

    fun SearchAuthors(username: String) {
        authorRepository.GetByUsername(username)
    }

    fun SearchGroupsByTitle(title: String) {

    }

    fun SearchGroupsByTags(tag: String) {
        groupRepository.GetGroupsByTag(tag)
    }

}