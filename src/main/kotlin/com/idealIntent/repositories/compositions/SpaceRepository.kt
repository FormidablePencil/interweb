package com.idealIntent.repositories.compositions

import com.idealIntent.models.compositionLayout.PrivilegedAuthorToSpacesModel
import com.idealIntent.models.privileges.CompositionSourceToLayout
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.CompositionCategory
import org.ktorm.dsl.*
import org.ktorm.schema.Column

class SpaceRepository(
    private val carouselOfImagesRepository: CarouselOfImagesRepository,
) : RepositoryBase() {
    private val space = carouselOfImagesRepository.space
    private val prvAuth2Space = PrivilegedAuthorToSpacesModel.aliased("prvAuth2Space")
    private val layout2Space = carouselOfImagesRepository.layout2Space
    private val layout = carouselOfImagesRepository.layout
    private val compSource2Layout = carouselOfImagesRepository.compSource2Layout
    private val compSource = carouselOfImagesRepository.compSource
    private val compInstance2compSource = carouselOfImagesRepository.compInstance2compSource
    private val compInstance = carouselOfImagesRepository.compInstance
    private val prvAth2CompSource = carouselOfImagesRepository.prvAth2CompSource

    private val img2Col = carouselOfImagesRepository.img2Col
    private val img = carouselOfImagesRepository.img
    private val text2Col = carouselOfImagesRepository.text2Col
    private val text = carouselOfImagesRepository.text
    private val author = carouselOfImagesRepository.author

    // space and layout
    fun getPublicLayoutOfCompositions(layoutId: Int): CompositionDataBuilder {
        TODO()
    }

    fun getPrivateLayoutOfCompositions(layoutId: Int, authorId: Int): CompositionDataBuilder {
        val layoutMetadata = getLayoutMetadata(
            spaceAddress = null,
            layoutId = layoutId,
            authorId = authorId
        )
        return getCompositionsOfLayout(layoutMetadata)
    }

    fun getSpaceLayoutPreviewOfCompositions(layoutId: Int, authorId: Int) {
    }

    fun getSpaceLayoutOfCompositions(spaceAddress: String, authorId: Int) {
        val layoutMetadata = getLayoutMetadata(
            spaceAddress = spaceAddress,
            layoutId = null,
            authorId = authorId
        )
        val composition = getCompositionsOfLayout(layoutMetadata)
    }

    /**
     * This is used to build queries with. Query from space.
     */
    fun queryFromSpace(): QuerySource {
        return database.from(space)
            .leftJoin(layout2Space, layout2Space.spaceAddress eq space.address)
            .leftJoin(layout, layout.id eq layout2Space.layoutId)
    }

    /**
     * This is used to build queries with. Query from layout.
     */
    fun fromLayout(): QuerySource {
        return database.from(layout)
    }


    /**
     * Query all ids of compositions in layout and category and type of composition.
     *
     * @param spaceAddress Query all compositions of the layout associated to the space with the [spaceAddress] provided.
     * @param layoutId Query all compositions of layout with id of [layoutId].
     * @param authorId Provide authorId for private layouts.
     * @return CompositionSourceToLayout, all [CompositionCategory ] to query through)
     */
    private fun getLayoutMetadata(
        spaceAddress: String?, layoutId: Int?, authorId: Int
    ): Pair<List<CompositionSourceToLayout>, Set<Pair<CompositionCategory, Int>>> {
        val listOfCompositionSourceToLayout = mutableListOf<CompositionSourceToLayout>()

        // region Used to find out what composition tables to search through
        val allTypesOfCompositionsLayoutContains = mutableSetOf<Pair<CompositionCategory, Int>>()
        // endregion
//        database.from(layout)

        val query: QuerySource = if (spaceAddress != null) queryFromSpace()
        else if (layoutId != null) fromLayout()
        else throw Exception("space nor layoutId was provided")

        query
            .leftJoinAuthor(authorId)
            .leftJoin(compSource2Layout, compSource2Layout.layoutId eq layout.id)
            .leftJoin(compSource, compSource.id eq compSource2Layout.sourceId)
            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .select(
                compSource2Layout.orderRank,
                compSource2Layout.sourceId,
                compInstance2compSource.compositionCategory,
                compInstance2compSource.compositionType,
            ).whereWithConditions {
                it += if (spaceAddress != null)
                    (space.address eq spaceAddress)
                else if (layoutId != null)
                    (layout.id eq layoutId)
                else throw Exception("space nor layoutId was provided")
            }
            .map {
                allTypesOfCompositionsLayoutContains += Pair(
                    CompositionCategory.fromInt(it[compInstance2compSource.compositionCategory]!!),
                    it[compInstance2compSource.compositionCategory]!!
                )

                listOfCompositionSourceToLayout += CompositionSourceToLayout(
                    sourceId = it[compSource2Layout.sourceId]!!,
                    compositionCategory = it[compInstance2compSource.compositionCategory]!!,
                    compositionType = it[compInstance2compSource.compositionType]!!,
                    orderRank = it[compSource2Layout.orderRank]!!
                )
            }
        return Pair(listOfCompositionSourceToLayout, allTypesOfCompositionsLayoutContains)
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
        val select = mutableSetOf<Column<out Any>>()
        val compositionBuilder = CompositionDataBuilder()

        select += carouselOfImagesRepository.compositionSelect

        database.from(compSource)
            .compositionsLeftJoinBuilder(compCategoryAndType)
            .select(select)
            .whereWithConditions {
                compositionSourceToLayout.forEach { item -> it += compSource.id eq item.sourceId }
                compositionWhereClauseBuilder(it, compCategoryAndType)
            }.map {
                println(it) // row should tell you what composition type and
                compositionMapBuilder(it, compCategoryAndType, compositionBuilder)
            }

        return compositionBuilder
    }




//    fun getPublicSpace(address: String) {
//        getSpace(address = address, authorId = null)
//    }
//
//    fun getPrivateSpace(address: String, authorId: Int) {
//        getSpace(address = address, authorId = authorId)
//    }


    private fun QuerySource.leftJoinAuthor(authorId: Int?): QuerySource {
        if (authorId != null)
            return leftJoin(prvAuth2Space, prvAuth2Space.authorId eq authorId)
        return this
    }
    // endregion Get


    // region Insert
    /**
     * Insert new layout
     *
     * @param name User defined space.
     */
    fun insertNewLayout(name: String): Int {
        return database.insertAndGenerateKey(layout) {
            set(layout.name, name)
        } as Int
    }

    fun insertNewSpace(): String {
        return database.insertAndGenerateKey(space) {} as String
    }

    fun associateCompositionToLayout(orderRank: Int, compositionSourceId: Int, layoutId: Int): Boolean {
        return database.insert(compSource2Layout) {
            set(compSource2Layout.sourceId, compositionSourceId)
            set(compSource2Layout.layoutId, layoutId)
            set(compSource2Layout.orderRank, orderRank)
        } == 1
    }

    fun associateLayoutToSpace(spaceAddress: String, layoutId: Int): Boolean {
        return database.insert(layout2Space) {
            set(layout2Space.spaceAddress, spaceAddress)
            set(layout2Space.layoutId, layoutId)
        } == 1
    }

    fun giveAuthorPrivilegesToPrivateSpace(spaceId: Int, authorId: Int): Boolean {
        return database.insert(prvAuth2Space) {
            set(prvAuth2Space.authorId, authorId)
            set(prvAuth2Space.spaceId, spaceId)
        } == 1
    }

    // endregion Insert
}