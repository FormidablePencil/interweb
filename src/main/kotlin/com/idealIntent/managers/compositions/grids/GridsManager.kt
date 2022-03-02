package com.idealIntent.managers.compositions.grids

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.dtos.compositions.grids.GridOneOffRes
import com.idealIntent.managers.compositions.CompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.grids.CompositionGrid
import dtos.compositions.grids.CompositionGrid.Basic
import kotlinx.serialization.Serializable

// temporary placeholders
data class GridResponse(val hello: Boolean)
data class GridRes(val hello: Boolean)

@Serializable
data class CreateGridReq(val hello: Boolean)

class GridsManager(
    private val oneOffGridManager: GridOneOffManager,
) : CompositionCategoryManagerStructure<CompositionGrid, GridOneOffRes, CompositionResponse>() {

    override fun getPublicComposition(
        compositionType: CompositionGrid,
        compositionSourceId: Int
    ): Pair<CompositionGrid, String> {
        TODO("Not yet implemented")
    }

    override fun getPrivateComposition(
        compositionType: CompositionGrid,
        compositionSourceId: Int,
        authorId: Int
    ): Pair<CompositionGrid, String> {
        return when (compositionType) {
            Basic ->
                Pair(
                    Basic,
                    gson.toJson(oneOffGridManager.getPrivateComposition(compositionSourceId, authorId))
                )
        }
    }

    override fun createComposition(
        compositionType: CompositionGrid,
        jsonData: String,
        layoutId: Int,
        authorId: Int
    ): Int {
        return when (compositionType) {
            Basic ->
                oneOffGridManager.createComposition(
                    gson.fromJson(jsonData, GridOneOffCreateReq::class.java), layoutId,
                    authorId
                )
        }
    }

    override fun updateComposition(
        compositionType: CompositionGrid,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) {
        when (compositionType) {
            Basic -> {
                TODO()
//                oneOffGridManager
            }
        }
    }

    override fun deleteComposition(
        compositionType: CompositionGrid,
        compositionSourceId: Int,
        authorId: Int
    ) {
        return when (compositionType) {
            Basic ->
                oneOffGridManager.deleteComposition(compositionSourceId, authorId)

        }
    }
}