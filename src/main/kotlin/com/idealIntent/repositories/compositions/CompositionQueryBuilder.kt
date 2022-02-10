package com.idealIntent.repositories.compositions

import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.QuerySource
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

class CompositionQueryBuilder(
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) {
    // region query builder
    // todo comment
    fun compositionSelect(): MutableSet<Column<out Any>> {
        val select = mutableSetOf<Column<out Any>>()
        select += carouselOfImagesRepository.compositionSelect
        return select
    }

    /**
     * Left joins compositions of category and type.
     *
     * Left joins all tables that compose a composition of category and type. For instance, if a composition is a basic image carousel,
     * it will all join text, text to collection relationship and text collection so all the records of a composition
     * were gotten of a layout.
     *
     * todo - many compositions will have the same tables to query it's records. At the moment will be duplicate of
     *  table join statements.
     *
     * @param compCategoryAndType Set of all types of compositions to join it related records to.
     */
    fun compositionsLeftJoinBuilder(
        querySource: QuerySource, compCategoryAndType: Set<Pair<CompositionCategory, Int>>,
    ): QuerySource {
        var res: QuerySource = querySource
        compCategoryAndType.forEach {
            when (it.first) {
                CompositionCategory.Carousel -> {
                    when (CompositionCarouselType.fromInt(it.second)) {
                        CompositionCarouselType.CarouselBlurredOverlay -> TODO()
                        CompositionCarouselType.CarouselMagnifying -> TODO()
                        CompositionCarouselType.BasicImages -> {
                            res = carouselOfImagesRepository.compositionLeftJoin(querySource)
                        }
                    }
                }
                CompositionCategory.Text -> TODO()
                CompositionCategory.Markdown -> TODO()
                CompositionCategory.Banner -> TODO()
                CompositionCategory.Grid -> TODO()
                CompositionCategory.Divider -> TODO()
                CompositionCategory.LineDivider -> TODO()
            }
        }
        return res
    }

    /**
     * Composition where clause builder.
     *
     * Build the where clause of the get composition query. Given all the types of composition types, this function will
     * return all logic of the where clause for the given compositions. For example, the carousel of images composition
     * with image and texts representing clickable images to redirect the user to a different page need to be queried
     * together so a where clause of image order rank is equal to text order rank.
     *
     * @param mutableList The list of where statements which will be processed to sql queries by ktorm.
     * @param compCategoryAndType
     */
    fun compositionWhereClauseBuilder(
        mutableList: MutableList<ColumnDeclaring<Boolean>>,
        compCategoryAndType: Set<Pair<CompositionCategory, Int>>,
    ) {
        compCategoryAndType.forEach {
            when (it.first) {
                CompositionCategory.Carousel -> {
                    when (CompositionCarouselType.fromInt(it.second)) {
                        CompositionCarouselType.CarouselBlurredOverlay -> TODO()
                        CompositionCarouselType.CarouselMagnifying -> TODO()
                        CompositionCarouselType.BasicImages ->
                            carouselOfImagesRepository.compositionWhereClause(mutableList)
                    }
                }
                CompositionCategory.Text -> TODO()
                CompositionCategory.Markdown -> TODO()
                CompositionCategory.Banner -> TODO()
                CompositionCategory.Grid -> TODO()
                CompositionCategory.Divider -> TODO()
                CompositionCategory.LineDivider -> TODO()
            }
        }
    }

    fun compositionMapBuilder(
        queryRowSet: QueryRowSet,
        compCategoryAndType: Set<Pair<CompositionCategory, Int>>,
        compositionBuilder: CompositionDataBuilder,
    ) {
        compCategoryAndType.forEach {
            when (it.first) {
                CompositionCategory.Carousel -> {
                    when (CompositionCarouselType.fromInt(it.second)) {
                        CompositionCarouselType.CarouselBlurredOverlay -> TODO()
                        CompositionCarouselType.CarouselMagnifying -> TODO()
                        CompositionCarouselType.BasicImages ->
                            carouselOfImagesRepository.compositionQueryMap(
                                queryRowSet,
                                compositionBuilder.carouselOfImagesData
                            )
                    }
                }
                CompositionCategory.Text -> TODO()
                CompositionCategory.Markdown -> TODO()
                CompositionCategory.Banner -> TODO()
                CompositionCategory.Grid -> TODO()
                CompositionCategory.Divider -> TODO()
                CompositionCategory.LineDivider -> TODO()
            }
        }
    }
    // endregion
}