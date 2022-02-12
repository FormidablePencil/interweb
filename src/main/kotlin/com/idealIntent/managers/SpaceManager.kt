package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.succeeded
import com.idealIntent.models.privileges.CompositionSourceToLayout
import com.idealIntent.repositories.compositions.CompositionDataBuilder
import com.idealIntent.repositories.compositions.CompositionQueryBuilder
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory
import org.ktorm.dsl.*

class SpaceManager(
    private val compositionQueryBuilder: CompositionQueryBuilder,
    private val appEnv: AppEnv,
    private val spaceRepository: SpaceRepository,
) {

    fun getPublicLayoutOfCompositions(layoutId: Int): CompositionDataBuilder {
        TODO()
    }

    fun getPrivateLayoutOfCompositions(layoutId: Int, authorId: Int): CompositionDataBuilder {
        val layoutMetadata = spaceRepository.getLayoutMetadata(
            spaceAddress = null,
            layoutId = layoutId,
            authorId = authorId
        )
        return getCompositionsOfLayout(layoutMetadata)
    }

    fun getSpaceLayoutPreviewOfCompositions(layoutId: Int, authorId: Int) {
    }

    fun getSpaceLayoutOfCompositions(spaceAddress: String, authorId: Int) {
        val layoutMetadata = spaceRepository.getLayoutMetadata(
            spaceAddress = spaceAddress,
            layoutId = null,
            authorId = authorId
        )
        val composition = getCompositionsOfLayout(layoutMetadata)
    }

    /**
     * Get compositions
     *
     * @param layoutMetadata List of composition source to layout info to query compositions and specify what
     * composition category and composition type to query from.
     */
    private fun getCompositionsOfLayout(
        layoutMetadata: Pair<List<CompositionSourceToLayout>, Set<Pair<CompositionCategory, Int>>>
    ): CompositionDataBuilder {
        val (compositionSourceToLayout, compCategoryAndType) = layoutMetadata
        val select = compositionQueryBuilder.compositionSelect()
        val compositionBuilder = CompositionDataBuilder()

        val query = compositionQueryBuilder.compositionsLeftJoinBuilder(appEnv.database.from(spaceRepository.compSource), compCategoryAndType)

        query.select(select)
            .whereWithConditions {
                compositionSourceToLayout.forEach { item -> it += spaceRepository.compSource.id eq item.sourceId }
                compositionQueryBuilder.compositionWhereClauseBuilder(it, compCategoryAndType)
            }.map {
                compositionQueryBuilder.compositionMapBuilder(it, compCategoryAndType, compositionBuilder)
            }

        return compositionBuilder
    }

    // todo associate space to author id
    fun createSpace(layoutName: String, authorId: Int): Int {
        TODO()
//        val spaceAddress = spaceRepository.insertNewSpace()
        val layoutId = spaceRepository.insertNewLayout(layoutName, authorId)
//        spaceRepository.associateLayoutToSpace(spaceAddress = spaceAddress, layoutId = layoutId)
    }
}