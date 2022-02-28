package integrationTests.compositions.texts

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.texts.TextLonelyCreateReq
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.texts.TextLonelyManager
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.texts.CompositionTextType
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow

class TextCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val textLonelyManager: TextLonelyManager by inject()
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
}