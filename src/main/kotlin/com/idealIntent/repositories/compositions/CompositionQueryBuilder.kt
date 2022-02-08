package com.idealIntent.repositories.compositions

import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarousel
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.QuerySource
import org.ktorm.schema.ColumnDeclaring

class CompositionQueryBuilder(
    val carouselOfImagesRepository: CarouselOfImagesRepository,
) {

    // region query builder
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
    private fun QuerySource.compositionsLeftJoinBuilder(
        compCategoryAndType: Set<Pair<CompositionCategory, Int>>,
    ): QuerySource {
        var res: QuerySource? = null
        compCategoryAndType.forEach {
            when (it.first) {
                CompositionCategory.Carousel -> {
                    when (CompositionCarousel.fromInt(it.second)) {
                        CompositionCarousel.CarouselBlurredOverlay -> TODO()
                        CompositionCarousel.CarouselMagnifying -> TODO()
                        CompositionCarousel.BasicImages -> {
                            res = carouselOfImagesRepository.compositionLeftJoin(this)
                        }
                    }
                }
                CompositionCategory.Text -> TODO()
                CompositionCategory.Markdown -> TODO()
                CompositionCategory.Banner -> TODO()
                CompositionCategory.OneOffGrid -> TODO()
                CompositionCategory.Divider -> TODO()
                CompositionCategory.LineDivider -> TODO()
            }
        }
        return res ?: this
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
    private fun compositionWhereClauseBuilder(
        mutableList: MutableList<ColumnDeclaring<Boolean>>,
        compCategoryAndType: Set<Pair<CompositionCategory, Int>>,
    ) {
        compCategoryAndType.forEach {
            when (it.first) {
                CompositionCategory.Carousel -> {
                    when (CompositionCarousel.fromInt(it.second)) {
                        CompositionCarousel.CarouselBlurredOverlay -> TODO()
                        CompositionCarousel.CarouselMagnifying -> TODO()
                        CompositionCarousel.BasicImages ->
                            carouselOfImagesRepository.compositionWhereClause(mutableList)
                    }
                }
                CompositionCategory.Text -> TODO()
                CompositionCategory.Markdown -> TODO()
                CompositionCategory.Banner -> TODO()
                CompositionCategory.OneOffGrid -> TODO()
                CompositionCategory.Divider -> TODO()
                CompositionCategory.LineDivider -> TODO()
            }
        }
    }

    private fun compositionMapBuilder(
        queryRowSet: QueryRowSet,
        compCategoryAndType: Set<Pair<CompositionCategory, Int>>,
        compositionBuilder: CompositionDataBuilder,
    ) {
        compCategoryAndType.forEach {
            when (it.first) {
                CompositionCategory.Carousel -> {
                    when (CompositionCarousel.fromInt(it.second)) {
                        CompositionCarousel.CarouselBlurredOverlay -> TODO()
                        CompositionCarousel.CarouselMagnifying -> TODO()
                        CompositionCarousel.BasicImages ->
                            carouselOfImagesRepository.compositionQueryMap(
                                queryRowSet,
                                compositionBuilder.carouselOfImagesData
                            )
                    }
                }
                CompositionCategory.Text -> TODO()
                CompositionCategory.Markdown -> TODO()
                CompositionCategory.Banner -> TODO()
                CompositionCategory.OneOffGrid -> TODO()
                CompositionCategory.Divider -> TODO()
                CompositionCategory.LineDivider -> TODO()
            }
        }
    }
    // endregion
}