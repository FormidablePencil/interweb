package integrationTests.compositions.headers

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.headers.HeaderBasicManager
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.headers.CompositionHeader
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow

class HeaderCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val headerBasicManager: HeaderBasicManager by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Header,
        compositionType = CompositionHeader.Basic.value,
    )

    companion object {
        private val gson = Gson()
        const val layoutName = "layout with header composition"
        val publicHeaderBasicReq = HeaderBasicCreateReq(
            bgImg = "bg img", profileImg = "profile img", privilegeLevel = 0, name = "name", listOf()
        )
        val privateHeaderBasicReq = publicHeaderBasicReq.let {
            HeaderBasicCreateReq(
                bgImg = it.bgImg, profileImg = it.profileImg, privilegeLevel = 1, name = it.name, listOf()
            )
        }
        val headerPublicBasicImagesReqSerialized: String = gson.toJson(publicHeaderBasicReq)
        val headerPrivateBasicImagesReqSerialized: String = gson.toJson(privateHeaderBasicReq)
    }


    // todo - creates only one variant. Implement more variants
    fun createComposition(public: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            compositionSerialized = if (public) headerPublicBasicImagesReqSerialized else headerPrivateBasicImagesReqSerialized,
            layoutId, authorId
        )
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")

        // validate that composition was created
        val resGetComp = if (public) headerBasicManager.getPublicComposition(res.data!!)
        else headerBasicManager.getPrivateComposition(res.data!!, authorId)

        resGetComp ?: throw failure("failed to get created composition")

        resGetComp.name shouldBe privateHeaderBasicReq.name
        resGetComp.bgImg shouldBe privateHeaderBasicReq.bgImg

        logInfo("Created composition: ${resGetComp.toString()}", this::class.java)

        return res.data!!
    }
}