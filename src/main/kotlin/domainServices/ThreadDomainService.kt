package domainServices

import managers.ThreadManager
import models.thread.Thread
import models.thread.ThreadComments
import repositories.ThreadRepository

class ThreadDomainService(
    private val threadRepository: ThreadRepository,
    private val threadManager: ThreadManager,
) {

    //region Get

    fun getThreadById(threadId: Int): Thread {
        var thread = threadRepository.GetThreadById(threadId)
        return thread
    }

    fun getThreadByAuthorsCategory(threadIds: List<Int>, category: List<String>): Thread {
        return threadRepository.FilterThreadByCategories(threadIds, category)
    }

    fun getSubThreads(subThreadsIds: List<Int>): List<Thread> {
        return threadRepository.GetThreads(subThreadsIds)
    }

    fun getRelatedThreads(relatedThreadsIds: List<Int>): List<Thread> {
        return threadRepository.GetThreads(relatedThreadsIds)
    }

    //endregion

    //region Create

    fun createThread(authorId: Int) {

    }

    //endregion

}