package managers

import repositories.ThreadRepository

class ThreadManager(
    private val threadingRepository: ThreadRepository,
) {
    fun GetThreadDataById(threadId: Int) {
        threadingRepository.GetThreadById(threadId)
    }
}