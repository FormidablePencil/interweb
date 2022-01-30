package integrationTests.space.flows

import com.idealIntent.dtos.compositions.CreateCompositionRequest
import com.idealIntent.dtos.compositions.CreateCompositionsRequest
import com.idealIntent.dtos.compositions.UserComposition
import com.idealIntent.services.CmsService
import dtos.compositions.CompositionCategory
import org.koin.test.inject
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.rollback

class CmsLibOfCompsFlow : BehaviorSpecFlow() {
    private val compositionService: CmsService by inject()

    private val createCompositionsRequest = CreateCompositionsRequest(
        spaceAddress = "some address",
        userCompositions = listOf(
            UserComposition(compositionType = CompositionCategory.Carousel, jsonData = ""),
            UserComposition(compositionType = CompositionCategory.Carousel, jsonData = ""),
            UserComposition(compositionType = CompositionCategory.Carousel, jsonData = "")
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
//            compositionService.batchCreateCompositions(request)
        }
    }

    suspend fun createComposition(
        request: CreateCompositionRequest = createCompositionRequest,
        cleanup: Boolean = false
    ) {
        return rollback(cleanup) {
//            compositionService.createComposition(request)
        }
    }
}