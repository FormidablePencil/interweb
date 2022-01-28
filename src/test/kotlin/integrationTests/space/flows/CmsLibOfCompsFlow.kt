package integrationTests.space.flows

import dtos.libOfComps.ComponentType
import org.koin.test.inject
import serialized.space.UserComponent
import serialized.space.CreateComponentRequest
import serialized.space.CreateComponentsRequest
import services.SpaceService
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class CmsLibOfCompsFlow : BehaviorSpecFlow() {
    private val spaceService: SpaceService by inject()

    private val createComponentsRequest = CreateComponentsRequest(
        spaceAddress = "some address",
        userComponents = listOf(
            UserComponent(componentType = ComponentType.CarouselOfImages, jsonData = ""),
            UserComponent(componentType = ComponentType.CarouselOfImages, jsonData = ""),
            UserComponent(componentType = ComponentType.CarouselOfImages, jsonData = "")
        )
    )
    private val createComponentRequest = CreateComponentRequest(
        userComponent = createComponentsRequest.userComponents[0],
        spaceAddress = createComponentsRequest.spaceAddress
    )

    suspend fun createBatchOfComponents(
        request: CreateComponentsRequest = createComponentsRequest,
        cleanup: Boolean = false
    ) {
        return rollback(cleanup) {
            spaceService.batchCreateComponents(request)
        }
    }

    suspend fun createComponent(request: CreateComponentRequest = createComponentRequest, cleanup: Boolean = false) {
        return rollback(cleanup) {
            spaceService.createComponent(request)
        }
    }
}