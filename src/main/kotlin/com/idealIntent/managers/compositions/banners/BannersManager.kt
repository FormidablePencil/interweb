package com.idealIntent.managers.compositions.banners

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.carousels.CompositionCarouselType

class BannersManager: ICompositionCategoryManagerStructure<CompositionBanner, CompositionResponse, CompositionResponse> {
    override fun getPublicComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int
    ): CarouselBasicImagesRes? {
        TODO("Not yet implemented")
    }

    override fun getPrivateComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int,
        authorId: Int
    ): CompositionResponse? {
        TODO("Not yet implemented")
    }

    override fun createComposition(
        compositionType: CompositionBanner,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun updateComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }
}