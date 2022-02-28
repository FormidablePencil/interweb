package com.idealIntent.managers.compositions.texts

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.texts.TextLonelyCreateReq
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.managers.compositions.CompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.texts.CompositionTextType
import dtos.compositions.texts.CompositionTextType.Basic

class TextsManager(
    private val textLonelyManager: TextLonelyManager,
) : CompositionCategoryManagerStructure<CompositionTextType, TextLonelyRes, CompositionResponse>() {

    override fun getPublicComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int
    ): TextLonelyRes? = when (compositionType) {
        Basic -> textLonelyManager.getPublicComposition(compositionSourceId)
    }

    override fun getPrivateComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int,
        authorId: Int
    ): TextLonelyRes? = when (compositionType) {
        Basic -> textLonelyManager.getPrivateComposition(compositionSourceId, authorId)
    }

    override fun createComposition(
        compositionType: CompositionTextType,
        jsonData: String,
        layoutId: Int,
        authorId: Int
    ): Int = when (compositionType) {
        Basic -> textLonelyManager.createComposition(
            gson.fromJson(jsonData, TextLonelyCreateReq::class.java), layoutId, authorId
        )
    }

    override fun updateComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) = when (compositionType) {
        Basic -> textLonelyManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
    }

    override fun deleteComposition(
        compositionType: CompositionTextType,
        compositionSourceId: Int,
        authorId: Int
    ) = when (compositionType) {
        Basic -> textLonelyManager.deleteComposition(compositionSourceId, authorId)
    }
}