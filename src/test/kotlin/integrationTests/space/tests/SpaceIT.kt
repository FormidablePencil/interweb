package integrationTests.space.tests

import com.idealIntent.services.CompositionService
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT

class SpaceIT : BehaviorSpecIT({
//    val spaceFlow: SpaceFlow by inject()
    val cmsService: CompositionService by inject()

    given("create new space with a layout of compositions") {
//        cmsService.createSpace(layoutName = "My first space")
    }
})