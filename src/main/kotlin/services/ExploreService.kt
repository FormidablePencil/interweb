package services

import models.thread.Thread
import repositories.profile.AuthorRepository
import repositories.SpaceRepository

class ExploreService(
    private var authorRepository: AuthorRepository,
    private var threadRepository: SpaceRepository,
) {

//    fun Search(search: String) {
//        SearchAuthors(search)
//        SearchThreadsByTitle(search)
//        SearchThreadsByTags(search)
//        // return the most relevant by precedence
//    }

//    fun SearchAuthors(email: String): Author {
//        val author = authorRepository.getByEmail(email)
//        if (author is Author)
//            return author
//        else throw Exception("failed to find author...")
//    }

    fun SearchThreadsByTitle(title: String): List<Thread> {
        return listOf<Thread>(Thread())
    }

    fun SearchThreadsByTags(tag: String) {
        threadRepository.GetThreadsByTag(tag)
    }

}