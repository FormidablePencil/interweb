package integrationTests.compositions.texts

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.texts.TextLonelyCreateReq
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.texts.TextLonelyManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.texts.TextLonelyRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.texts.CompositionTextType
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow

class TextCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val textLonelyManager: TextLonelyManager by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val textLonelyRepository: TextLonelyRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val signupFlow: SignupFlow by inject()
    private val textCompositionsFlow: TextCompositionsFlow by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Text,
        compositionType = CompositionTextType.Basic.value,
    )

    companion object {
        private val gson = Gson()
        const val layoutName = "layout with text composition"
        val publicTextLonelyCreateReq = TextLonelyCreateReq(
            name = "lonely text",
            text = "a lonely text",
            privilegeLevel = 0,
            privilegedAuthors = listOf(),
        )
        val privateTextLonelyCreateReq = publicTextLonelyCreateReq.let {
            TextLonelyCreateReq(
                name = it.name,
                text = it.text,
                privilegeLevel = 1,
                privilegedAuthors = it.privilegedAuthors,
            )
        }
        val publicTextLonelyCreateReqSerialized: String = gson.toJson(publicTextLonelyCreateReq)
        val privateTextLonelyCreateReqSerialized: String = gson.toJson(privateTextLonelyCreateReq)

        fun validateDataResponse(res: TextLonelyRes, isPublic: Boolean = true) {
            publicTextLonelyCreateReq.let {
                res.name shouldBe it.name
                res.text shouldBe it.text
                res.privilegeLevel shouldBe it.privilegeLevel
//                        it.privilegedAuthors.forEach { item ->
//                            publicTextLonelyCreateReq.privilegedAuthors.findLast {
//                                item.username shouldBe
//                            }
//                        }
            }
        }
    }

    fun createComposition(public: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            compositionSerialized = if (public) publicTextLonelyCreateReqSerialized else privateTextLonelyCreateReqSerialized,
            layoutId, authorId
        )
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")

        // validate that composition was created
        val resGetComp = if (public) textLonelyManager.getPublicComposition(res.data!!)
        else textLonelyManager.getPrivateComposition(res.data!!, authorId)

        resGetComp ?: throw failure("failed to get created composition")

        resGetComp.name shouldBe privateTextLonelyCreateReq.name
        resGetComp.text shouldBe privateTextLonelyCreateReq.text

        logInfo("Created composition: $resGetComp", this::class.java)

        return res.data!!
    }

    suspend fun prepareComposition(res: TextLonelyCreateReq): Pair<TextLonelyCreateReq, Int> {
        val authorId = signupFlow.signupReturnId()
        val layoutId = spaceRepository.insertNewLayout(publicTextLonelyCreateReq.name, authorId)

        val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
            compositionType = 0,
            authorId = authorId,
            name = "legit",
            privilegeLevel = 0,
        )

        spaceRepository.associateCompositionToLayout(
            orderRank = 0,
            compositionSourceId = compositionSourceId,
            layoutId = layoutId
        )

        return Pair(publicTextLonelyCreateReq, compositionSourceId)
    }
}