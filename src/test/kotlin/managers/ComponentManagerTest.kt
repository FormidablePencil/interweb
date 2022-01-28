import com.google.gson.Gson
import dtos.libOfComps.ComponentType
import dtos.libOfComps.carousels.CarouselBasicImages
import dtos.libOfComps.genericStructures.Image
import dtos.libOfComps.genericStructures.PrivilegedAuthor
import dtos.libOfComps.genericStructures.Text
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.spyk
import managers.ComponentManager
import repositories.SpaceRepository
import repositories.components.BannerRepository
import repositories.components.CarouselRepository
import serialized.libOfComps.UserComponent
import serialized.libOfComps.CreateComponentRequest

class ComponentManagerTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk()
    val bannerRepository: BannerRepository = mockk()
    val carouselRepository: CarouselRepository = mockk()

    val spaceService = spyk(ComponentManager(spaceRepository, bannerRepository, carouselRepository))

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