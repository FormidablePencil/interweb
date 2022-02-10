package com.idealIntent.managers.compositions

import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.managers.compositions.grids.CreateGridReq
import com.idealIntent.managers.compositions.grids.GridRes
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import org.koin.core.component.KoinComponent

class BannerBasicManager: ICompositionTypeManagerStructure<GridRes, IImagesCarouselEntity,
        CreateGridReq, CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    override fun getPublicComposition(compositionSourceId: Int): GridRes? {
        TODO("Not yet implemented")
    }

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): GridRes? {
        TODO("Not yet implemented")
    }

    override fun createComposition(createRequest: CreateGridReq, layoutId: Int, authorId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        TODO("Not yet implemented")
    }

}