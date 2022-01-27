package managers

import com.google.gson.Gson
import dtos.libOfComps.ComponentType
import dtos.libOfComps.banners.BannerBasic
import dtos.libOfComps.carousels.CarouselBasicImages
import repositories.SpaceRepository
import repositories.components.BannerRepository
import repositories.components.CarouselRepository
import serialized.space.CreateComponent
import serialized.space.CreateComponentRequest

class ComponentManager(
    private val spaceRepository: SpaceRepository,
    private val bannerRepository: BannerRepository,
    private val carouselRepository: CarouselRepository,
) {
    fun getComponents() {
        // given some identifiers
        // get data of ComponentType by identifier
    }

    fun createComponent(request: CreateComponent, spaceAddress: String): Boolean {
        return createComponent(request)
    }

    fun batchCreateComponents(request: List<CreateComponent>, spaceAddress: String) {
        // todo - revert if some component fails to save. Save all or save non
        request.map {
            createComponent(it)
        }
    }

    private fun createComponent(request: CreateComponent): Boolean {
        val gson = Gson()
        return when (request.componentType) {
            // region Carousels
            ComponentType.CarouselBlurredOverlay,
            ComponentType.CarouselOfImages -> {
                return carouselRepository.insertCarouselBasicImages(
                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java)
                ) != null
            }
            // endregion

            // region Just texts
            ComponentType.Markdown -> TODO()
            ComponentType.BasicText -> TODO()
            // endregion

            // region Banners
            ComponentType.BasicBanners ->
                bannerRepository.createBannerBasic(
                    gson.fromJson(request.jsonData, BannerBasic::class.java)
                ) != null
            // endregion

            // region Grids
            ComponentType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
            // endregion
        }
    }

    fun deleteComponent(request: CreateComponent): Boolean {
        val gson = Gson()
        return when (request.componentType) {
            // region Carousels
            ComponentType.CarouselBlurredOverlay,
            ComponentType.CarouselOfImages ->
                TODO()
//                carouselRepository.deleteCarouselOfImagesById(
//                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java),
//                )
            // endregion

            // region Texts
            ComponentType.Markdown -> TODO()
            ComponentType.BasicBanners ->
                TODO("code")
//                bannerRepository.deleteBannerBasic(
//                    gson.fromJson(request.jsonData, BannerBasic::class.java)
//                )
            ComponentType.BasicText -> TODO()
            // endregion

            // region Grids
            ComponentType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
            // endregion
        }
    }


    fun batchDeleteComponents() {
        // given the ids of components and the given ComponentType
        // delete a list of components from all categories of where each is requested
    }
}