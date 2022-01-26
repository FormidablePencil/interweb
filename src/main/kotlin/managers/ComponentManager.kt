package managers

import com.google.gson.Gson
import dtos.libOfComps.ComponentType
import dtos.libOfComps.banners.BannerBasic
import dtos.libOfComps.carousels.CarouselBasicImages
import dtos.libOfComps.carousels.CarouselBlurredOverlay
import repositories.SpaceRepository
import repositories.components.BannerRepository
import repositories.components.CarouselRepository
import serialized.space.CreateComponentRequest

class ComponentManager(
    private val spaceRepository: SpaceRepository,
    private val bannerRepository: BannerRepository,
    private val carouselRepository: CarouselRepository,
) {
    fun createComponent(request: CreateComponentRequest): Boolean {
        val gson = Gson()
        return when (request.componentType) {
            // Carousels
            ComponentType.CarouselOfImages -> {
                val results = carouselRepository.createCarouselBasicImages(
                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java)
                )
                println("ids")
                println(results)
                println("ids")
                return true
            }
            ComponentType.CarouselBlurredOverlay ->
                carouselRepository.createCarouselBlurredOverlay(
                    gson.fromJson(request.jsonData, CarouselBlurredOverlay::class.java)
                )

            // Just text
            ComponentType.Markdown -> TODO()
            ComponentType.BasicText -> TODO()

            // Banners
            ComponentType.BasicBanners ->
                bannerRepository.createBannerBasic(
                    gson.fromJson(request.jsonData, BannerBasic::class.java)
                )

            // Grids
            ComponentType.OneOffGrid -> TODO()

            // Dividers
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
        }
    }

    fun deleteComponent(request: CreateComponentRequest): Boolean {
        val gson = Gson()
        return when (request.componentType) {
            ComponentType.CarouselOfImages ->
                TODO("code")
//                carouselRepository.deleteCarouselOfImagesById(
//                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java),
//                )
            ComponentType.Markdown -> TODO()
            ComponentType.BasicBanners ->
                TODO("code")
//                bannerRepository.deleteBannerBasic(
//                    gson.fromJson(request.jsonData, BannerBasic::class.java)
//                )
            ComponentType.BasicText -> TODO()
            ComponentType.OneOffGrid -> TODO()
            ComponentType.CarouselBlurredOverlay -> TODO()
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
        }
    }
}