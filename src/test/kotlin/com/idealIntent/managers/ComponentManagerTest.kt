import com.idealIntent.managers.CompositionManager
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.spyk

class CompositionManagerTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk()
    val bannerRepository: BasicBannerRepository = mockk()
    val imageRepository: ImageRepository = mockk()
    val carouselRepository: CarouselOfImagesRepository = mockk()
    val textRepository: TextRepository = mockk()
    val privilegeRepository: PrivilegeRepository = mockk()

    val componentManager = spyk(
        CompositionManager(
            spaceRepository,
            bannerRepository,
            carouselRepository,
            imageRepository,
            textRepository,
            privilegeRepository,
        )
    )

    beforeEach { }

    xgiven("userCompositions") {
//        val gson = Gson()
//
//        val image = Image(description = "image description", url = "image url", orderRank = 1, id = 1)
//        val navTo = Text(orderRank = 10000, text = "some link")
//        val privilegedAuthors = PrivilegesModel(authorId = 1, modLvl = 2)
//
//        val req = CreateCompositionRequest(
//            spaceAddress = "SDLFJEI",
//            userComposition = UserComposition(
//                compositionType = CompositionCategory.Carousel,
//                jsonData = gson.toJson(
//                    CarouselBasicImages(
//                        title = "project images",
//                        images = listOf(image),
//                        navToCorrespondingImagesOrder = listOf(navTo),
//                        privilegedAuthors = listOf(privilegedAuthors)
//                    )
//                )
//            ),
//        )
////        val res = componentManager.createComposition(req.userComposition, req.spaceAddress)
    }

    xgiven("deleteComposition") { }
})