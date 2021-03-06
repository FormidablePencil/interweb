package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.models.privileges.CompositionSourceToLayout
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
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

    private val compSource = CompositionSourceRepository.compSource

    /**
     * Get space layout of compositions
     *
     * @param spaceAddress
     * @return Space's layout of composition.
     */
    fun getSpaceLayoutOfCompositions(spaceAddress: Int): CompositionDataBuilder {
        TODO()
    }

    /**
     * Get private space layout of compositions
     *
     * @param spaceAddress
     * @param authorId
     * @return Space's layout of composition.
     */
    fun getPrivateSpaceLayoutOfCompositions(spaceAddress: Int, authorId: Int): CompositionDataBuilder {
        TODO()
    }

    /**
     * Get private layout of compositions
     *
     * For CMS purposes. Use [getPrivateSpaceLayoutOfCompositions] for user querying purposes.
     *
     * @param layoutId Id of layout.
     * @param authorId Id of author privileged to layout.
     * @return Layout of compositions.
     */
    fun getPrivateLayoutOfCompositions(layoutId: Int, authorId: Int): CompositionDataBuilder {
        val layoutMetadata = spaceRepository.getPrivateLayoutMetadataById(
            layoutId = layoutId,
            authorId = authorId
        )
        return getCompositionsOfLayout(layoutMetadata)
    }

    fun getSpaceLayoutPreviewOfCompositions(layoutId: Int, authorId: Int) {
    }

    fun getSpaceLayoutOfCompositions(spaceAddress: String, authorId: Int) {
        val layoutMetadata = spaceRepository.getPrivateLayoutMetadataBySpace(
            spaceAddress = spaceAddress,
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

        val query = compositionQueryBuilder.compositionsLeftJoinBuilder(
            appEnv.database.from(compSource), compCategoryAndType
        )

        query.select(select)
            .whereWithOrConditions {
                compositionSourceToLayout.forEach { item -> it += compSource.id eq item.sourceId }
            }
            .whereWithConditions {
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