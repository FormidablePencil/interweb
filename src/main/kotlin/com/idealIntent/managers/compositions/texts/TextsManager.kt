package com.idealIntent.managers.compositions.texts

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.carousels.CompositionCarouselType
import dtos.compositions.texts.CompositionTextType

class TextsManager :
    ICompositionCategoryManagerStructure<CompositionTextType, CompositionResponse, CompositionResponse> {
    override fun getPublicComposition(
        compositionType: CompositionCarouselType,
        compositionSourceId: Int
    ): CarouselBasicImagesRes? {
        TODO("Not yet implemented")
    }

    override fun getPrivateComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int,
        authorId: Int
    ): CompositionResponse? {
        TODO("Not yet implemented")
    }

    override fun createComposition(
        compositionType: CompositionTextType,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): Int {
        TODO("Organized the file structure first.")
//        val gson = Gson()
//        return lonelyTextManager.createComposition(
//            gson.fromJson(jsonData, CreateCarouselBasicImagesReq::class.java),
//            layoutId
//        )
    }

    override fun updateComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }
}