package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo

class CarouselBlurredOverlayRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val imageRepository: ImageRepository by inject()
    private val textRepository: TextRepository by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val compositionService: CompositionService by inject()
    private val signupFlow: SignupFlow by inject()
    private val carouselCompositionFlow: CarouselCompositionsFlow by inject()

    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Carousel,
        compositionType = CompositionCarouselType.BasicImages.value,
    )
}
