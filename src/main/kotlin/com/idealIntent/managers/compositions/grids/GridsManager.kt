package com.idealIntent.managers.compositions.grids

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
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
) : CompositionCategoryManagerStructure<CompositionGrid, GridRes, CompositionResponse>() {

    override fun getPublicComposition(
        compositionType: CompositionGrid,
        compositionSourceId: Int
    ): GridRes? {
        TODO("Not yet implemented")
    }

    override fun getPrivateComposition(
        compositionType: CompositionGrid,
        compositionSourceId: Int,
        authorId: Int
    ): GridRes? {
        return when (compositionType) {
            Basic -> {
                oneOffGridManager.getPrivateComposition(compositionSourceId, authorId)
            }
        }
    }

    override fun createComposition(
        compositionType: CompositionGrid,
        jsonData: String,
        layoutId: Int,
        userId: Int
    ): Int {
        return when (compositionType) {
            Basic -> {
                oneOffGridManager.createComposition(
                    gson.fromJson(jsonData, CreateGridReq::class.java), layoutId,
                    userId
                )
            }
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
            Basic -> {
                oneOffGridManager.deleteComposition(compositionSourceId, authorId)
            }
        }
    }
}