package com.idealIntent.managers

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.BatchUpdateCompositionRequest
import com.idealIntent.dtos.compositions.BatchUpdateCompositionsRequest
import com.idealIntent.dtos.compositions.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.UpdateCompositionRequest
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.banners.BasicBannerRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionType
import dtos.compositions.banners.BannerBasic
import dtos.compositions.carousels.CarouselBasicImages
import dtos.compositions.carousels.CarouselOfImagesTABLE
import dtos.space.IUserComposition

// todo - comments
// todo - move this logic to services
class CompositionManager(
    private val spaceRepository: SpaceRepository,
    private val bannerRepository: BasicBannerRepository,
    private val carouselRepository: CarouselOfImagesRepository,
    private val imageRepository: ImageRepository,
    private val textRepository: TextRepository,
    private val privilegeRepository: PrivilegeRepository,
) {
    fun getComposition() {
        // given some identifiers
        // get data of CompositionType by identifier
    }

    fun createComposition(request: IUserComposition, spaceAddress: String): Boolean {
        return createComposition(request)
    }

    fun batchCreateCompositions(request: List<IUserComposition>, spaceAddress: String) {
        // todo - revert if some component fails to save. Save all or save non
        request.map {
            createComposition(it)
        }
    }

    private fun createComposition(request: IUserComposition): Boolean {
        val gson = Gson()
        return when (request.compositionType) {
            // region Carousels
            CompositionType.CarouselBlurredOverlay,
            CompositionType.CarouselOfImages -> {
                return carouselRepository.insertNewComposition(
                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java)
                ) != null
            }
            // endregion

            // region Just texts
            CompositionType.Markdown -> TODO()
            CompositionType.BasicText -> TODO()
            // endregion

            // region Banners
            CompositionType.BasicBanners ->
                bannerRepository.createBannerBasic(
                    gson.fromJson(request.jsonData, BannerBasic::class.java)
                ) != null
            // endregion

            // region Grids
            CompositionType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionType.Divider -> TODO()
            CompositionType.LineDivider -> TODO()
            // endregion
        }
    }


    fun deleteComposition(request: IUserComposition): Boolean {
        val gson = Gson()
        return when (request.compositionType) {
            // region Carousels
            CompositionType.CarouselBlurredOverlay,
            CompositionType.CarouselOfImages ->
                TODO()
//                carouselRepository.deleteComposition(
//                    gson.fromJson(request.jsonData, CarouselBasicImages::class.java),
//                )
            // endregion

            // region Texts
            CompositionType.Markdown -> TODO()
            CompositionType.BasicBanners ->
                TODO("code")
//                bannerRepository.deleteBannerBasic(
//                    gson.fromJson(request.jsonData, BannerBasic::class.java)
//                )
            CompositionType.BasicText -> TODO()
            // endregion

            // region Grids
            CompositionType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionType.Divider -> TODO()
            CompositionType.LineDivider -> TODO()
            // endregion
        }
    }


    fun updateComposition(request: UpdateCompositionRequest) {
        request.updateComposition.map {
            updateComposition(it)
        }
    }

    fun updateComposition(request: SingleUpdateCompositionRequest): Boolean {
        val gson = Gson()
        when (CompositionType.fromInt(request.compositionType)) {
            // region Carousels
            CompositionType.CarouselBlurredOverlay,
            CompositionType.CarouselOfImages -> {
                carouselRepository.update(
                    componentId = request.id,
                    column = CarouselOfImagesTABLE.fromInt(request.where[0].table),
                    updateToData = request.updateToData
                )
            }
            // endregion

            // region Just texts
            CompositionType.Markdown -> TODO()
            CompositionType.BasicText -> TODO()
            // endregion

            // region Banners
            CompositionType.BasicBanners -> TODO()
            // endregion

            // region Grids
            CompositionType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionType.Divider -> TODO()
            CompositionType.LineDivider -> TODO()
            // endregion
        }
        return false
    }

    fun batchUpdateCompositions(request: BatchUpdateCompositionsRequest) {
        request.updateComposition.map {
            batchUpdateComposition(it)
        }
    }

    fun batchUpdateComposition(request: BatchUpdateCompositionRequest): Boolean {
        val gson = Gson()
        when (CompositionType.fromInt(request.compositionType)) {
            // region Carousels
            CompositionType.CarouselBlurredOverlay,
            CompositionType.CarouselOfImages -> {
                carouselRepository.batchUpdate(
                    componentId = request.id,
                    table = CarouselOfImagesTABLE.fromInt(request.where[0].table),
                    updateToData = request.updateToData
                )
            }
            // endregion

            // region Just texts
            CompositionType.Markdown,
            CompositionType.BasicText ->
                textRepository.batchUpdateRecords(
                    records = request.updateToData,
                    id = request.id
                )
            // endregion

            // region Banners
            CompositionType.BasicBanners -> TODO()
            // endregion

            // region Grids
            CompositionType.OneOffGrid -> TODO()
            // endregion

            // region Dividers
            CompositionType.Divider -> TODO()
            CompositionType.LineDivider -> TODO()
            // endregion
        }
        return false
    }
}