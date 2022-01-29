package integrationTests.space.flows

import dtos.compositions.CompositionType
import org.koin.test.inject
import com.idealIntent.dtos.compositions.UserComposition
import com.idealIntent.dtos.compositions.CreateCompositionRequest
import com.idealIntent.dtos.compositions.CreateCompositionsRequest
import com.idealIntent.services.SpaceService
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class CmsLibOfCompsFlow : BehaviorSpecFlow() {
    private val spaceService: SpaceService by inject()

    private val createCompositionsRequest = CreateCompositionsRequest(
        spaceAddress = "some address",
        userCompositions = listOf(
            UserComposition(compositionType = CompositionType.CarouselOfImages, jsonData = ""),
            UserComposition(compositionType = CompositionType.CarouselOfImages, jsonData = ""),
            UserComposition(compositionType = CompositionType.CarouselOfImages, jsonData = "")
        )
    )
    private val createCompositionRequest = CreateCompositionRequest(
        userComposition = createCompositionsRequest.userCompositions[0],
        spaceAddress = createCompositionsRequest.spaceAddress
    )

    suspend fun createBatchOfCompositions(
        request: CreateCompositionsRequest = createCompositionsRequest,
        cleanup: Boolean = false
    ) {
        return rollback(cleanup) {
            spaceService.batchCreateCompositions(request)
        }
    }

    suspend fun createComposition(request: CreateCompositionRequest = createCompositionRequest, cleanup: Boolean = false) {
        return rollback(cleanup) {
            spaceService.createComposition(request)
        }
    }
}