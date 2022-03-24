package integrationTests.compositions.banners

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.banners.BannerImageCreateReq
import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.banners.BannerImageManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.CompositionBanner
import integrationTests.auth.flows.AuthUtilities
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
        fun validateDataResponse(res: BannerImageRes, public: Boolean = true) {
            publicBannerImageCreateReq.let {
                res.name shouldBe it.name
                if (public) res.privilegeLevel shouldBe it.privilegeLevel
                else res.privilegeLevel shouldBe privateBannerImageCreateReq.privilegeLevel
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
            privilegeLevel = 0,
            name = "banner image",
            privilegedAuthors = listOf(),
        )
        val privateBannerImageCreateReq = publicBannerImageCreateReq.let {
            BannerImageCreateReq(
                imageUrl = it.imageUrl,
                imageAlt = it.imageAlt,
                privilegeLevel = 1,
                name = it.name,
                privilegedAuthors = it.privilegedAuthors,
            )
        }
        val publicBannerImagesReqCreateSerialized: String = gson.toJson(publicBannerImageCreateReq)
        val privateBannerImagesReqCreateSerialized: String = gson.toJson(privateBannerImageCreateReq)
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

        resGetComp.imageUrl shouldBe privateBannerImageCreateReq.imageUrl
        resGetComp.imageAlt shouldBe privateBannerImageCreateReq.imageAlt

        if (public) resGetComp.privilegeLevel shouldBe publicBannerImageCreateReq.privilegeLevel
        else resGetComp.privilegeLevel shouldBe privateBannerImageCreateReq.privilegeLevel

        val privilegedAuthors = mutableListOf<PrivilegedAuthor>()
        privilegedAuthors += privateBannerImageCreateReq.privilegedAuthors
        privilegedAuthors += listOf(
            PrivilegedAuthor(
                username = AuthUtilities.createAuthorRequest.username,
                modify = 1,
                deletion = 1,
                modifyUserPrivileges = 1,
            )
        )

        resGetComp.privilegedAuthors shouldBe privilegedAuthors
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