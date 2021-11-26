package repositories

import models.thread.Thread

class ThreadRepository {

    //region Get

    fun GetThreadById(id: Int): Thread {
        return Thread()
    }

    fun GetThreads(threadsIds: List<Int>): List<Thread> {
        return emptyList<Thread>()
    }

    fun GetThreadsByTag(author: String) {

    }

    fun FilterThreadByCategories(threadIds: List<Int>, category: List<String>): Thread {
        return Thread()
    }

    fun GetThreadByAuthorsTags(authorId: Int, tag: String): List<Thread> {
        throw Exception()
    }

    //endregion Get

    fun CreateThread() {

    }

    fun DeleteThread() {

    }

}