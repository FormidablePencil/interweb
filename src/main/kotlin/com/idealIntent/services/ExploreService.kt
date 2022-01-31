package com.idealIntent.services

import com.idealIntent.repositories.profile.AuthorRepository
import com.idealIntent.repositories.SpaceRepository

class ExploreService(
    private var authorRepository: AuthorRepository,
    private var spaceRepository: SpaceRepository,
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
        spaceRepository.getSpace(tag)
    }

}