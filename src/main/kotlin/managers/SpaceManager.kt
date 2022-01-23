package managers

import repositories.SpaceRepository

class SpaceManager(
    private val spaceRepository: SpaceRepository,
) {
    fun GetSpaceDataById(threadId: Int) {
//        spaceRepository.GetThreadById(threadId)
    }
}