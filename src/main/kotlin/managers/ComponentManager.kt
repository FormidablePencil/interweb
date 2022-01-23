package managers

import libOfComps.ComponentType
import libOfComps.banners.BannerBasic
import libOfComps.carousels.CarouselFlat
import libOfComps.carousels.CarouselMagnifying
import repositories.components.BannerRepository
import repositories.components.CarouselRepository
import repositories.SpaceRepository
import serialized.space.CreateComponentRequest

class ComponentManager(
    private val spaceRepository: SpaceRepository,
    private val bannerCompsRepository: BannerRepository,
    private val carouselCompsRepository: CarouselRepository,
) {
    fun createComponent(request: CreateComponentRequest): Boolean {
        return when (ComponentType.fromInt(request.componentType)) {
            ComponentType.CarouselFlat ->
                carouselCompsRepository.createCarouselFlat(request.jsonData as CarouselFlat)
            ComponentType.CarouselMagnifying ->
                carouselCompsRepository.createCarouselMagnifying(request.jsonData as CarouselMagnifying)
            ComponentType.Markdown -> TODO()
            ComponentType.BasicBanners ->
                bannerCompsRepository.createBannerBasic(request.jsonData as BannerBasic)
            ComponentType.BasicText -> TODO()
            ComponentType.OneOffGrid -> TODO()
            ComponentType.CarouselBlurredOverlay -> TODO()
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
        }
    }

    fun deleteComponent(request: CreateComponentRequest): Boolean {
        return when (ComponentType.fromInt(request.componentType)) {
            ComponentType.CarouselFlat ->
                carouselCompsRepository.deleteCarouselFlat(request.jsonData as CarouselFlat)
            ComponentType.CarouselMagnifying ->
                carouselCompsRepository.deleteCarouselMagnifying(request.jsonData as CarouselMagnifying)
            ComponentType.Markdown -> TODO()
            ComponentType.BasicBanners ->
                bannerCompsRepository.deleteBannerBasic(request.jsonData as BannerBasic)
            ComponentType.BasicText -> TODO()
            ComponentType.OneOffGrid -> TODO()
            ComponentType.CarouselBlurredOverlay -> TODO()
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
        }
    }
}