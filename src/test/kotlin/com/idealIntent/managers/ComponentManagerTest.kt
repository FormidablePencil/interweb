import com.google.gson.Gson
import com.idealIntent.managers.CompositionManager
import dtos.compositions.CompositionType
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.genericStructures.images.Image
import dtos.compositions.genericStructures.privileges.PrivilegedAuthor
import dtos.compositions.genericStructures.texts.Text
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.spyk
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.compositions.*
import com.idealIntent.dtos.compositions.CreateCompositionRequest
import com.idealIntent.dtos.compositions.UserComposition
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository

class CompositionManagerTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk()
    val bannerRepository: BasicBannerRepository = mockk()
    val imageRepository: ImageRepository = mockk()
    val carouselRepository: CarouselOfImagesRepository = mockk()
    val textRepository: TextRepository = mockk()
    val privilegeRepository: PrivilegeRepository = mockk()

    val spaceService = spyk(
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
        val gson = Gson()

        val image = Image(imageTitle = "image title", imageUrl = "image url", orderRank = 1)
        val navTo = Text(orderRank = 10000, text = "some link")
        val privilegedAuthors = PrivilegedAuthor(authorId = 1, modLvl = 2)

        val req = CreateCompositionRequest(
            spaceAddress = "SDLFJEI",
            userComposition = UserComposition(
                compositionType = CompositionType.CarouselOfImages,
                jsonData = gson.toJson(
                    CarouselBasicImages(
                        title = "project images",
                        images = listOf(image),
                        navToCorrespondingImagesOrder = listOf(navTo),
                        privilegedAuthors = listOf(privilegedAuthors)
                    )
                )
            ),
        )
        val res = spaceService.createComposition(req.userComposition, req.spaceAddress)
    }

    xgiven("deleteComposition") { }
})