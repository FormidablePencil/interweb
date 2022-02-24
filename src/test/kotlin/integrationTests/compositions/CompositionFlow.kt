package integrationTests.compositions

import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.carouselPrivateBasicImagesReqSerialized
import shared.testUtils.carouselPublicBasicImagesReqSerialized
import shared.testUtils.createPrivateCarouselBasicImagesReq

class CompositionFlow : BehaviorSpecFlow() {
}