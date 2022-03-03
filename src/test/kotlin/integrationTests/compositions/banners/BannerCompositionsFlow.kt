package integrationTests.compositions.banners

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.banners.BannerImageCreateReq
import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.banners.BannerImageManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.banners.BannerImageRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.CompositionBanner
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow

class BannerCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val bannerImageManager: BannerImageManager by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val signupFlow: SignupFlow by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Banner,
        compositionType = CompositionBanner.Basic.value,
    )

    companion object {
        fun validateDataResponse(res: BannerImageRes) {
            publicBannerImageCreateReq.let {
                res.name shouldBe it.name
                res.privilegeLevel shouldBe it.privilegeLevel
            }

//                        it.privilegedAuthors.forEach { item ->
//                            publicTextLonelyCreateReq.privilegedAuthors.findLast {
//                                item.username shouldBe
//                            }
//                        }
        }

        private val gson = Gson()
        const val layoutName = "That was legitness"
        val publicBannerImageCreateReq = BannerImageCreateReq(
            imageUrl = "image url",
            imageAlt = "image alt",
            privilegeLevel = 1,
            name = "banner image",
            privilegedAuthors = listOf(),
        )
        val privateHeaderImageCreateReq = publicBannerImageCreateReq.let {
            BannerImageCreateReq(
                imageUrl = it.imageUrl,
                imageAlt = it.imageAlt,
                privilegeLevel = it.privilegeLevel,
                name = it.name,
                privilegedAuthors = it.privilegedAuthors,
            )
        }
        val publicBannerImagesReqCreateSerialized: String = gson.toJson(publicBannerImageCreateReq)
        val privateBannerImagesReqCreateSerialized: String = gson.toJson(privateHeaderImageCreateReq)
    }


    // todo - creates only one variant. Implement more variants
    fun createComposition(public: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            compositionSerialized = if (public) publicBannerImagesReqCreateSerialized else privateBannerImagesReqCreateSerialized,
            layoutId, authorId
        )
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")

        // validate that composition was created
        val resGetComp = if (public) bannerImageManager.getPublicComposition(res.data!!)
        else bannerImageManager.getPrivateComposition(res.data!!, authorId)

        resGetComp ?: throw failure("failed to get created composition")

        resGetComp.imageUrl shouldBe privateHeaderImageCreateReq.imageUrl
        resGetComp.imageAlt shouldBe privateHeaderImageCreateReq.imageAlt
        resGetComp.privilegeLevel shouldBe privateHeaderImageCreateReq.privilegeLevel
        resGetComp.privilegedAuthors shouldBe privateHeaderImageCreateReq.privilegedAuthors

        logInfo("Created composition: $resGetComp", this::class.java)

        return res.data!!
    }

    suspend fun prepareComposition(): Pair<BannerImageCreateReq, Int> {
        val authorId = signupFlow.signupReturnId()
        val layoutId = spaceRepository.insertNewLayout(publicBannerImageCreateReq.name, authorId)

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

        return Pair(publicBannerImageCreateReq, compositionSourceId)
    }
}