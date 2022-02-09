package com.idealIntent.managers.compositions.carousels

import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.compositions.ICompositionManagerStructure
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesComposePrepared
import org.koin.core.component.KoinComponent

class CarouselBlurredOverlayManager: ICompositionManagerStructure<CarouselBasicImagesRes, IImagesCarouselEntity, CreateCarouselBasicImagesReq,
        CarouselOfImagesComposePrepared, CompositionResponse>, KoinComponent {
    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): CarouselBasicImagesRes? {
        TODO("Not yet implemented")
    }

    override fun createComposition(
        createRequest: CreateCarouselBasicImagesReq,
        layoutId: Int,
        authorId: Int
    ): CompositionResponse {
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

    override fun deleteComposition(compositionSourceId: Int, authorId: Int): Boolean {
        TODO("Not yet implemented")
    }
}