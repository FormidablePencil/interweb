package com.idealIntent.repositories

import com.idealIntent.dtos.space.CreateSpaceRequest
import com.idealIntent.models.space.ISpaceEntity
import com.idealIntent.models.space.SpacesModel
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

/**
 * Space repository
 *
 * Each space consist of one compositional layout and a compositional layout consists of compositions.
 */
class SpaceRepository : RepositoryBase() {
    private val Database.spaces get() = this.sequenceOf(SpacesModel)

    fun getSpace(address: String): ISpaceEntity? {
        return database.spaces.find { it.address eq address }
    }

    fun insertSpace(space: CreateSpaceRequest, address: String): Boolean {
        return database.insert(SpacesModel) {
            set(it.address, address)
            set(it.authorId, space.authorId)
            set(it.jsonData, space.jsonData)
        } != 0
    }

//    /**
//     * Get layouts of space
//     *
//     * @param spaceAddress
//     */
//    fun getSpaceLayout(spaceAddress: String): ISpaceEntity? {
//        return database.spaces.find { it.address eq spaceAddress }
//    }
    // todo - there are 2 different kinds of spaces. Author space (multiple spaces), public space

    /**
     * Associate a layout to space
     *
     * @param layoutId Id of composition layout
     * @param spaceAddress Space to associate to by space's address
     */
    fun addLayoutToSpace(layoutId: Int, spaceAddress: String) {
    }

    // space own layouts.
    // layout owns compositions (components)
    //
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
