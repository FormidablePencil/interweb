package integrationTests.space.flows

import org.koin.test.inject
import com.idealIntent.services.SpaceService
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class SpaceFlow: BehaviorSpecFlow() {
    private val spaceService: SpaceService by inject()

    suspend fun createSpace(
        cleanup: Boolean = false
    ) {
        return rollback(cleanup) {
//            val space:  Space {
//
//        }
//            spaceService.createNewSpace()
        }
    }

    fun get() {
    }

    fun update() {}

    fun softDelete() {}
}