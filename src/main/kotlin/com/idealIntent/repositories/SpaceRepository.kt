package com.idealIntent.repositories

import models.space.Space
import models.space.Spaces
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import com.idealIntent.dtos.space.CreateSpaceRequest

class SpaceRepository : RepositoryBase() {
    private val Database.spaces get() = this.sequenceOf(Spaces)

    fun getSpace(address: String): Space? {
        return database.spaces.find { it.address eq address }
    }

    fun insertSpace(space: CreateSpaceRequest, address: String): Boolean {
        return database.insert(Spaces) {
            set(it.address, address)
            set(it.authorId, space.authorId)
            set(it.jsonData, space.jsonData)
        } != 0
    }
}

//    fun getSpacesByAuthor(authorId: Int): Space? {
//        return database.spaces.find { it.authorId eq authorId }
//    }

//    fun softDeleteSpace() {
//    }

//    fun GetThreads(threadsIds: List<Int>): List<Thread> {
//        return emptyList<Thread>()
//    }
//
//    fun GetThreadsByTag(author: String) {
//
//    }
//
//    fun FilterThreadByCategories(threadIds: List<Int>, category: List<String>): Thread {
//        return Thread()
//    }
//
//    fun GetThreadByAuthorsTags(authorId: Int, tag: String): List<Thread> {
//        throw Exception()
//    }
