// =========
// Please do not make changes to this file directly. This is a generated file.
//
// This is a test of composition repository - BannerImageRepository.
// =========
package com.idealIntent.repositories.compositions.banners

import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.services.CompositionService
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.banners.BannerCompositionsFlow
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecUtRepo2
import shared.testUtils.rollback

public class BannerImageRepositoryTestGen() : BehaviorSpecUtRepo2() {
  init {
    // region Setup
    val bannerImageRepository: BannerImageRepository by inject()
    val compositionService: CompositionService by inject()
    val signupFlow: SignupFlow by inject()
    val bannerCompositionsFlow: BannerCompositionsFlow by inject()

    suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> { 
      val authorId = signupFlow.signupReturnId()
        val layoutId = compositionService.createNewLayout(
          name = BannerCompositionsFlow.layoutName,
          authorId = authorId
      ).data ?: throw failure("Failed to get id of newly created layout.")
        
      val compositionSourceId = bannerCompositionsFlow.createComposition(publicView, layoutId,
        authorId)
      
      return Triple(compositionSourceId, layoutId, authorId)
    }

    beforeEach { clearAllMocks() }
    // endregion Setup


    given("getPublicComposition") {

      then("failed to get because composition is private") {
        rollback {
          val (compositionSourceId, _, _) = signup_then_createComposition(false)

          bannerImageRepository.getPublicComposition(
              compositionSourceId = compositionSourceId
          ) shouldBe null
        }
      }

      then("successfully got public composition") {
        rollback {
          val (compositionSourceId, _, _) = signup_then_createComposition(true)

          val res: BannerImageRes = bannerImageRepository.getPublicComposition(
              compositionSourceId = compositionSourceId
          ) ?: throw failure("failed to get composition")

          BannerCompositionsFlow.validateDataResponse(res)
        }
      }
    }

    given("getPrivateComposition") {

      then("successfully got private composition") {
        rollback {
          val (compositionSourceId, _, authorId) = signup_then_createComposition(false)

          val res: BannerImageRes =bannerImageRepository.getPrivateComposition(
              compositionSourceId, authorId
          ) ?: throw failure("failed to get composition")

          BannerCompositionsFlow.validateDataResponse(res)
        }
      }
    }

    given("compose") {

      then("successfully composed collections and compositions as one composition") {
        rollback {
          val (preparedComposition, sourceId) = bannerCompositionsFlow.prepareComposition()

          bannerImageRepository.compose(preparedComposition, sourceId)
        }
      }
    }

    given("deleteComposition") {

      then("failed to delete because author id provided is not privileged to delete.") {
        rollback {
          val (compositionSourceId, _, _) = signup_then_createComposition(true)

          val ex = shouldThrowExactly<CompositionException> {
              bannerImageRepository.deleteComposition(compositionSourceId, 999999999)
          }.code shouldBe CompositionCode.CompositionNotFound
        }
      }

      then("failed to delete on an id of a composition that doesn't exist") {
        rollback {
          val (_, _, authorId) = signup_then_createComposition(true)

          val ex = shouldThrowExactly<CompositionException> {
              bannerImageRepository.deleteComposition(999999999, authorId)
          }.code shouldBe CompositionCode.CompositionNotFound
        }
      }

      then("success") {
        rollback {
          val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

          // region before deletion assertions
          val res: BannerImageRes = bannerImageRepository.getPublicComposition(
              compositionSourceId = compositionSourceId
          ) ?: throw failure("failed to get composition")

          BannerCompositionsFlow.validateDataResponse(res)
          // endregion

          bannerImageRepository.deleteComposition(compositionSourceId, authorId)

          // region after deletion assertions
          val resAfterDeletion = bannerImageRepository.getPublicComposition(compositionSourceId)

          resAfterDeletion shouldBe null
          // endregion
        }
      }
    }
  }
}
