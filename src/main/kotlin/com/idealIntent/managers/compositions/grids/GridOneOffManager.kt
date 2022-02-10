package com.idealIntent.managers.compositions.grids

import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.managers.compositions.ICompositionTypeManagerStructure
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import org.koin.core.component.KoinComponent

class GridOneOffManager : ICompositionTypeManagerStructure<GridRes, IImagesCarouselEntity,
        CreateGridReq, CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    override fun createComposition(
        createRequest: CreateGridReq,
        layoutId: Int,
        authorId: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun batchUpdateCompositions(id: Int, records: List<RecordUpdate>): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): GridRes? {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int): Boolean {
        TODO("Not yet implemented")
    }
}