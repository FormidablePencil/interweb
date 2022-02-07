package integrationTests.space.tests

import com.idealIntent.services.CmsService
import com.idealIntent.services.SpacesService
import integrationTests.auth.flows.LoginFlow
import integrationTests.space.flows.SpaceFlow
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT

class SpaceIT : BehaviorSpecIT({
//    val spaceFlow: SpaceFlow by inject()
    val cmsService: CmsService by inject()

    given("create new space with a layout of compositions") {
        cmsService.createSpace(layoutName = "My first space")
    }
})