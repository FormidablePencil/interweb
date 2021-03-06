package com.idealIntent.managers.compositions.headers

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.managers.compositions.CompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.headers.CompositionHeader
import dtos.compositions.headers.CompositionHeader.Basic

class HeadersManager(
    private val headerBasicManager: HeaderBasicManager,
) : CompositionCategoryManagerStructure<CompositionHeader, HeaderBasicRes, CompositionResponse>() {
    override fun getPublicComposition(
        compositionType: CompositionHeader,
        compositionSourceId: Int
    ): Pair<CompositionHeader, String> = when (compositionType) {
        Basic ->
            Pair(
                Basic,
                gson.toJson(headerBasicManager.getPublicComposition(compositionSourceId))
            )
    }

    override fun getPrivateComposition(
        compositionType: CompositionHeader,
        compositionSourceId: Int,
        authorId: Int
    ): Pair<CompositionHeader, String> = when (compositionType) {
        Basic ->
            Pair(
                Basic,
                gson.toJson(headerBasicManager.getPrivateComposition(compositionSourceId, authorId))
            )
    }

    override fun createComposition(
        compositionType: CompositionHeader,
        jsonData: String,
        layoutId: Int,
        authorId: Int
    ): Int = when (compositionType) {
        Basic ->
            headerBasicManager.createComposition(
                gson.fromJson(jsonData, HeaderBasicCreateReq::class.java), layoutId, authorId
            )
    }

    override fun updateComposition(
        compositionType: CompositionHeader,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) = when (compositionType) {
        Basic ->
            headerBasicManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
    }

    override fun deleteComposition(
        compositionType: CompositionHeader,
        compositionSourceId: Int,
        authorId: Int
    ) = when (compositionType) {
        Basic ->
            headerBasicManager.deleteComposition(compositionSourceId, authorId)
    }
}