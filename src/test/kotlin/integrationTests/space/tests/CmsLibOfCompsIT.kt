package integrationTests.space.tests

import dtos.libOfComps.ComponentType
import integrationTests.space.flows.CmsLibOfCompsFlow
import io.kotest.matchers.shouldBe
import org.koin.test.inject
import com.idealIntent.serialized.libOfComps.CreateComponentRequest
import com.idealIntent.serialized.libOfComps.UserComponent
import com.idealIntent.serialized.space.GetSpaceRequest
import com.idealIntent.services.SpaceService
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class CmsLibOfCompsIT : BehaviorSpecIT() {
    private val cmsLibOfCompsFlow = CmsLibOfCompsFlow()
    private val spaceService: SpaceService by inject()

    init {
        val components = listOf(
            UserComponent(componentType = ComponentType.CarouselOfImages, jsonData = ""),
            UserComponent(componentType = ComponentType.CarouselOfImages, jsonData = ""),
            UserComponent(componentType = ComponentType.CarouselOfImages, jsonData = "")
        )
        given("test that all components of libOfComps work") {
            components.forEach {
                then("create a single component ${it.componentType.name}") {
                    rollback {
                        val spaceAddress = ""

                        val createCompRes = cmsLibOfCompsFlow.createComponent(
                            CreateComponentRequest(spaceAddress, userComponent = it)
                        )
                        createCompRes shouldBe true
                        val result = spaceService.getSpaceByAddress(GetSpaceRequest(address = spaceAddress))
//                    result.
                    }
                }
            }
        }

        given("create multiple components") {
            then("success") {
                rollback {

                }
            }
        }
    }
}