package com.idealIntent.managers.compositions.texts

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.texts.TextLonelyCreateReq
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.CompositionTypeManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.texts.TextLonelyRepository
import dtos.compositions.texts.CompositionTextType

class TextLonelyManager(
    private val textLonelyRepository: TextLonelyRepository,
    private val spaceRepository: SpaceRepository,
    private val compositionPrivilegesManager: CompositionPrivilegesManager,
) : CompositionTypeManagerStructure<TextLonelyRes, IImagesCarouselEntity, TextLonelyCreateReq, TextLonelyRes,
        CompositionResponse>() {

    override fun getPublicComposition(compositionSourceId: Int): TextLonelyRes? =
        textLonelyRepository.getPublicComposition(compositionSourceId)

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): TextLonelyRes? =
        textLonelyRepository.getPrivateComposition(compositionSourceId, authorId)

    override fun createComposition(createRequest: TextLonelyCreateReq, layoutId: Int, authorId: Int): Int {
        appEnv.database.useTransaction {
            val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
                compositionType = CompositionTextType.Basic.value,
                privilegeLevel = createRequest.privilegeLevel,
                name = createRequest.name,
                authorId = authorId,
            )

            spaceRepository.associateCompositionToLayout(
                orderRank = 0,
                compositionSourceId = compositionSourceId,
                layoutId = layoutId
            )

            textLonelyRepository.compose(
                composePrepared = createRequest,
                sourceId = compositionSourceId
            )

            compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                createRequest.privilegedAuthors, compositionSourceId, authorId
            )

            return compositionSourceId
        }
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