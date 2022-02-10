package com.idealIntent.managers.compositions.texts

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.ICompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.texts.CompositionText

class TextsManager: ICompositionCategoryManagerStructure<CompositionText, CompositionResponse, CompositionResponse> {
    override fun getPrivateComposition(
        compositionType: CompositionText,
        compositionSourceId: Int,
        authorId: Int
    ): CompositionResponse? {
        TODO("Not yet implemented")
    }

    override fun createComposition(
        compositionType: CompositionText,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): CompositionResponse {
        TODO("Not yet implemented")
    }

    override fun updateComposition(
        compositionType: CompositionText,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(
        compositionType: CompositionText,
        compositionSourceId: Int,
        authorId: Int
    ): Boolean {
        TODO("Not yet implemented")
    }
}