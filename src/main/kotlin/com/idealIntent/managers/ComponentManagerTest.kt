import com.google.gson.Gson
import dtos.libOfComps.ComponentType
import dtos.libOfComps.carousels.CarouselBasicImages
import dtos.libOfComps.genericStructures.images.Image
import dtos.libOfComps.genericStructures.privileges.PrivilegedAuthor
import dtos.libOfComps.genericStructures.texts.Text
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.spyk
import com.idealIntent.managers.ComponentManager
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.components.*
import com.idealIntent.serialized.libOfComps.CreateComponentRequest
import com.idealIntent.serialized.libOfComps.UserComponent

class ComponentManagerTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk()
    val bannerRepository: BannerRepository = mockk()
    val imageRepository: ImageRepository = mockk()
    val carouselRepository: CarouselRepository = mockk()
    val textRepository: TextRepository = mockk()
    val privilegeRepository: PrivilegeRepository = mockk()

    val spaceService = spyk(
        ComponentManager(
            spaceRepository,
            bannerRepository,
            carouselRepository,
            imageRepository,
            textRepository,
            privilegeRepository,
        )
    )

    beforeEach { }

    xgiven("userComponents") {
        val gson = Gson()

        val image = Image(imageTitle = "image title", imageUrl = "image url", orderRank = 1)
        val navTo = Text(orderRank = 10000, text = "some link")
        val privilegedAuthors = PrivilegedAuthor(authorId = 1, modLvl = 2)

        val req = CreateComponentRequest(
            spaceAddress = "SDLFJEI",
            userComponent = UserComponent(
                componentType = ComponentType.CarouselOfImages,
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
        val res = spaceService.createComponent(req.userComponent, req.spaceAddress)
    }

    xgiven("deleteComponent") { }
})