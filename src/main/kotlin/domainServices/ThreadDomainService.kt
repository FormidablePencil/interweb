package domainServices

import managers.ThreadManager
import models.Thread
import models.ThreadComments
import repositories.ThreadRepository

class ThreadDomainService(
    private val threadRepository: ThreadRepository,
    private val threadManager: ThreadManager,
) {

    //region Get

    fun GetThreadById(threadId: Int): Thread {
        var thread = threadRepository.GetThreadById(threadId)
        return thread
    }

    fun GetThreadByAuthorsCategory(threadIds: List<Int>, category: List<String>): Thread {
        return threadRepository.FilterThreadByCategories(threadIds, category)
    }

    fun GetSubThreads(subThreadsIds: List<Int>): List<Thread> {
        return threadRepository.GetThreads(subThreadsIds)
    }

    fun GetRelatedThreads(relatedThreadsIds: List<Int>): List<Thread> {
        return threadRepository.GetThreads(relatedThreadsIds)
    }

    //endregion

    //region Create

    fun CreateThread(authorId: Int) {

    }

    //endregion

}