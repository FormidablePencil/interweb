// =========
// Please do not make changes to this file directly. This is a generated file.
//
// This is a test of composition repository - TextLonelyRepository.
// =========
package com.idealIntent.repositories.compositions.texts

import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.services.CompositionService
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.texts.TextCompositionsFlow
import integrationTests.compositions.texts.TextCompositionsFlow.Companion.privateTextLonelyCreateReq
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecUtRepo2
import shared.testUtils.rollback

public class TextLonelyRepositoryTestGen() : BehaviorSpecUtRepo2() {
  init {
    // region Setup
    val textLonelyRepository: TextLonelyRepository by inject()
    val compositionService: CompositionService by inject()
    val signupFlow: SignupFlow by inject()
    val textCompositionsFlow: TextCompositionsFlow by inject()

    suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> { 
      val authorId = signupFlow.signupReturnId()
        val layoutId = compositionService.createNewLayout(
          name = TextCompositionsFlow.layoutName,
          authorId = authorId
      ).data ?: throw failure("Failed to get id of newly created layout.")
        
      val compositionSourceId = textCompositionsFlow.createComposition(publicView, layoutId,
        authorId)
      
      return Triple(compositionSourceId, layoutId, authorId)
    }

    beforeEach { clearAllMocks() }
    // endregion Setup


    given("getPublicComposition") {

      then("failed to get because composition is private") {
        rollback {
          val (compositionSourceId, _, _) = signup_then_createComposition(false)

          textLonelyRepository.getPublicComposition(
              compositionSourceId = compositionSourceId
          ) shouldBe null 
        }
      }

      then("successfully got public composition") {
        rollback {
          val (compositionSourceId, _, _) = signup_then_createComposition(true)

          val res: TextLonelyRes = textLonelyRepository.getPublicComposition(
              compositionSourceId = compositionSourceId
          ) ?: throw failure("failed to get composition")

          TextCompositionsFlow.validateDataResponse(res)
        }
      }
    }

    given("getPrivateComposition") {

      then("successfully got private composition") {
        rollback {
          val (compositionSourceId, _, authorId) = signup_then_createComposition(false)

          val res: TextLonelyRes =textLonelyRepository.getPrivateComposition(
              compositionSourceId, authorId
          ) ?: throw failure("failed to get composition")

          TextCompositionsFlow.validateDataResponse(res)
        }
      }
    }

    given("compose") {

      then("successfully composed collections and compositions as one composition") {
        rollback {
          val (preparedComposition, sourceId) =
              textCompositionsFlow.prepareComposition(privateTextLonelyCreateReq)

          textLonelyRepository.compose(preparedComposition, sourceId)
        }
      }
    }

    given("deleteComposition") {

      then("failed to delete because author id provided is not privileged to delete.") {
        rollback {
          val (compositionSourceId, _, _) = signup_then_createComposition(true)

          val ex = shouldThrowExactly<CompositionException> {
              textLonelyRepository.deleteComposition(compositionSourceId, 999999999)
          }.code shouldBe CompositionCode.CompositionNotFound
        }
      }

      then("failed to delete on an id of a composition that doesn't exist") {
        rollback {
          val (_, _, authorId) = signup_then_createComposition(true)

          val ex = shouldThrowExactly<CompositionException> {
              textLonelyRepository.deleteComposition(999999999, authorId)
          }.code shouldBe CompositionCode.CompositionNotFound
        }
      }

      then("success") {
        rollback {
          val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

          // region before deletion assertions
          val resBeforeDeletion: TextLonelyRes = textLonelyRepository.getPublicComposition(
              compositionSourceId = compositionSourceId
          ) ?: throw failure("failed to get composition")

          TextCompositionsFlow.validateDataResponse(resBeforeDeletion)
          // endregion

          textLonelyRepository.deleteComposition(compositionSourceId, authorId)

          // region after deletion assertions
          val resAfterDeletion = textLonelyRepository.getPublicComposition(compositionSourceId)

          resAfterDeletion shouldBe null
          // endregion
        }
      }
    }
  }
}
