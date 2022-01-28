package com.idealIntent.managers

import com.google.gson.Gson
import dtos.libOfComps.ComponentType
import dtos.libOfComps.banners.BannerBasic
import dtos.libOfComps.carousels.CarouselBasicImages
import dtos.libOfComps.carousels.CarouselOfImagesTABLE
import dtos.space.IUserComponent
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.components.*
import com.idealIntent.serialized.libOfComps.BatchUpdateComponentRequest
import com.idealIntent.serialized.libOfComps.BatchUpdateComponentsRequest
import com.idealIntent.serialized.libOfComps.SingleUpdateComponentRequest
import com.idealIntent.serialized.libOfComps.UpdateComponentsRequest

class ComponentManager(
    private val spaceRepository: SpaceRepository,
    private val bannerRepository: BannerRepository,
    private val carouselRepository: CarouselRepository,
    private val imageRepository: ImageRepository,
    private val textRepository: TextRepository,
    private val privilegeRepository: PrivilegeRepository,
) {
    fun getComponents() {
        // given some identifiers
        // get data of ComponentType by identifier
    }

    fun createComponent(request: IUserComponent, spaceAddress: String): Boolean {
        return createComponent(request)
    }

    fun batchCreateComponents(request: List<IUserComponent>, spaceAddress: String) {
        // todo - revert if some component fails to save. Save all or save non
        request.map {
            createComponent(it)
        }
    }

    private fun createComponent(request: IUserComponent): Boolean {
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


    fun deleteComponent(request: IUserComponent): Boolean {
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


    fun updateComponents(request: UpdateComponentsRequest) {
        request.updateComponent.map {
            updateComponent(it)
        }
    }

    fun updateComponent(request: SingleUpdateComponentRequest): Boolean {
        val gson = Gson()
        when (ComponentType.fromInt(request.componentType)) {
            // region Carousels
            ComponentType.CarouselBlurredOverlay,
            ComponentType.CarouselOfImages -> {
                carouselRepository.update(
                    componentId = request.componentId,
                    column = CarouselOfImagesTABLE.fromInt(request.where[0].table),
                    updateToData = request.updateToData
                )
            }
            // endregion

            // region Just texts
            ComponentType.Markdown -> TODO()
            ComponentType.BasicText -> TODO()
            // endregion

            // region Banners
            ComponentType.BasicBanners -> TODO()
            // endregion

            // region Grids
            ComponentType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
            // endregion
        }
        return false
    }

    fun batchUpdateComponents(request: BatchUpdateComponentsRequest) {
        request.updateComponent.map {
            batchUpdateComponent(it)
        }
    }

    fun batchUpdateComponent(request: BatchUpdateComponentRequest): Boolean {
        val gson = Gson()
        when (ComponentType.fromInt(request.componentType)) {
            // region Carousels
            ComponentType.CarouselBlurredOverlay,
            ComponentType.CarouselOfImages -> {
                carouselRepository.batchUpdate(
                    componentId = request.componentId,
                    table = CarouselOfImagesTABLE.fromInt(request.where[0].table),
                    updateToData = request.updateToData
                )
            }
            // endregion

            // region Just texts
            ComponentType.Markdown,
            ComponentType.BasicText ->
                textRepository.batchUpdateTexts(
                    collectionId = request.componentId,
                    records = request.updateToData
                )
            // endregion

            // region Banners
            ComponentType.BasicBanners -> TODO()
            // endregion

            // region Grids
            ComponentType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            ComponentType.Divider -> TODO()
            ComponentType.LineDivider -> TODO()
            // endregion
        }
        return false
    }
}