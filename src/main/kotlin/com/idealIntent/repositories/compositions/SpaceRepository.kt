package com.idealIntent.repositories.compositions

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositionLayout.CompositionLayoutsModel
import com.idealIntent.models.compositionLayout.LayoutToSpacesModel
import com.idealIntent.models.compositionLayout.PrivilegedAuthorToLayoutsModel
import com.idealIntent.models.compositionLayout.PrivilegedAuthorToSpacesModel
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.privileges.CompositionSourceToLayout
import com.idealIntent.models.space.SpacesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import dtos.compositions.CompositionCategory
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

// todo - author space. Special kind of space...
class SpaceRepository : RepositoryBase() {
    private val Database.spaces get() = this.sequenceOf(SpacesModel)
    private val Database.prvAuth2Layout get() = this.sequenceOf(PrivilegedAuthorToLayoutsModel)

    companion object {
        val space = SpacesModel.aliased("space")
        val prvAuth2Space = PrivilegedAuthorToSpacesModel.aliased("prvAuth2Space")
        val layout2Space = LayoutToSpacesModel.aliased("layout2Space")
        val layout = CompositionLayoutsModel.aliased("layout")
        val prvAuth2Layout = PrivilegedAuthorToLayoutsModel.aliased("prvAuth2Layout")
    }

    private val compSource2Layout = CompositionSourceRepository.compSource2Layout
    private val compSource = CompositionSourceRepository.compSource
    private val prvAth2CompSource = CompositionSourceRepository.prvAth2CompSource

    private val compInstance2compSource = CompositionSourceRepository.compInstance2compSource
    private val compInstance = ImagesCarouselsModel.aliased("compInstance")

    private val img2Col = ImageRepository.img2Col
    private val img = ImageRepository.img
    private val text2Col = TextRepository.text2Col
    private val text = TextRepository.text
    private val author = AuthorProfileRelatedRepository.author


    // region Used to build queries with
    fun queryFromSpace(): QuerySource {
        return database.from(space)
            .leftJoin(layout2Space, layout2Space.spaceAddress eq space.address)
            .leftJoin(layout, layout.id eq layout2Space.layoutId)
    }

    fun fromLayout(): QuerySource {
        return database.from(layout)
    }

    private fun QuerySource.leftJoinAuthor(authorId: Int?): QuerySource {
        if (authorId != null)
            return leftJoin(prvAuth2Space, prvAuth2Space.authorId eq authorId)
        return this
    }
    // endregion


    // region Layout metadata
    // todo - Make it so that it is used in a few different ways.
    //  1. Query by space address with author id or not for public/private purposes.
    //  2. Query by layout with author id or without for public/private purpose of cms.

    /**
     * Get public metadata of layout by space address.
     */
    fun getPublicLayoutMetadataBySpace(spaceAddress: String) =
        getLayoutMetadata(spaceAddress = spaceAddress, layoutId = null, authorId = null)

    /**
     * Get private metadata of layout by space address. Provide privileged [authorId].
     */
    fun getPrivateLayoutMetadataBySpace(spaceAddress: String, authorId: Int) =
        getLayoutMetadata(spaceAddress = spaceAddress, layoutId = null, authorId = authorId)

    /**
     * Get public metadata of layout by layout id.
     */
    fun getPublicLayoutMetadataById(layoutId: Int) =
        getLayoutMetadata(spaceAddress = null, layoutId = layoutId, authorId = null)

    /**
     * Get private metadata of layout by layout's id. Provide privileged [authorId].
     */
    fun getPrivateLayoutMetadataById(layoutId: Int, authorId: Int) =
        getLayoutMetadata(spaceAddress = null, layoutId = layoutId, authorId = authorId)

    /**
     * Query all ids of compositions in layout and category and type of composition.
     *
     * @param spaceAddress Query all compositions of the layout associated to the space with the [spaceAddress] provided.
     * @param layoutId Query all compositions of layout with id of [layoutId].
     * @param authorId Provide authorId for private layouts.
     * @return CompositionSourceToLayout, all [CompositionCategory ] to query through
     */
    private fun getLayoutMetadata(
        spaceAddress: String?, layoutId: Int?, authorId: Int?
    ): Pair<List<CompositionSourceToLayout>, Set<Pair<CompositionCategory, Int>>> {
        val listOfCompositionSourceToLayout = mutableListOf<CompositionSourceToLayout>()

        // region Used to find out what composition tables to search through
        val allTypesOfCompositionsLayoutContains = mutableSetOf<Pair<CompositionCategory, Int>>()
        // endregion

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
    // endregion Layout metadata


    // region Insert new
    /**
     * Insert new layout. Give layout a [name] and assign it to [authorId].
     * @return Id of newly created layout.
     */
    fun insertNewLayout(name: String, authorId: Int): Int {
        val layoutId = database.insertAndGenerateKey(layout) {
            set(layout.name, name)
        } as Int

        if (database.insert(prvAuth2Layout) {
                set(prvAuth2Layout.authorId, authorId)
                set(prvAuth2Layout.layoutId, layoutId)
                set(prvAuth2Layout.modify, 1)
                set(prvAuth2Layout.modifyUserPrivileges, 1)
            } != 1) throw CompositionExceptionReport(
            CompositionCode.FailedToAssociateAuthorToLayout, authorId.toString(), this::class.java
        )
        return layoutId
    }

    /**
     * Insert new space and give [authorId] absolute privileges.
     *
     * @return Address of the newly created space.
     */
    fun insertNewSpace(authorId: Int): Int {
        val spaceAddress = database.insertAndGenerateKey(space) {} as Int
        database.insert(prvAuth2Space) { set(prvAuth2Space.spaceId, spaceAddress) }
        return spaceAddress
    }
    // endregion Insert new


    // region Associate
    /**
     * Associate composition to layout.
     *
     * @param orderRank For ordering compositions in layout.
     * @param compositionSourceId Id of composition source to associate to layout.
     * @param layoutId Id of layout to associate composition to.
     * @return Success or failed. Fails if compositionSourceId or layoutId is invalid. Both of these ids are foreign keys.
     */
    fun associateCompositionToLayout(orderRank: Int, compositionSourceId: Int, layoutId: Int): Boolean {
        return database.insert(compSource2Layout) {
            set(compSource2Layout.sourceId, compositionSourceId)
            set(compSource2Layout.layoutId, layoutId)
            set(compSource2Layout.orderRank, orderRank)
        } == 1
    }

    /**
     * Associate layout to space.
     *
     * Each space has one layout but each layout may have multiple spaces associated to.
     *
     * [spaceAddress] and [layoutId] are foreign keys.
     */
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
    // endregion Associate


    // region Validate
    /**
     * Validate that [authorId] is privileged to modify layout of [layoutId]. Used to check privileges before modifying.
     *
     *  [layoutId] and [authorId] are foreign keys.
     */
    fun validateAuthorPrivilegedToModifyLayout(layoutId: Int, authorId: Int) =
        database.prvAuth2Layout.find {
            it.authorId eq authorId and
                    (it.layoutId eq layoutId) and
                    (it.modify eq 1)
        } != null
    // endregion Validate
}


//    fun GetThreadsByTag(author: String) {
//    fun FilterThreadByCategories(threadIds: List<Int>, category: List<String>): Thread {
//    fun GetThreadByAuthorsTags(authorId: Int, tag: String): List<Thread> {
