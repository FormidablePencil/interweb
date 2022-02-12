package com.idealIntent.repositories.compositions

import com.idealIntent.dtos.space.CreateSpaceRequest
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositionLayout.*
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.models.privileges.CompositionSourceToLayout
import com.idealIntent.models.privileges.CompositionSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorToCompositionSourcesModel
import com.idealIntent.models.space.ISpaceEntity
import com.idealIntent.models.space.SpacesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import dtos.compositions.CompositionCategory
import models.profile.AuthorsModel
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class SpaceRepository(
    imageRepository: ImageRepository,
    textRepository: TextRepository,
) : RepositoryBase() {
    private val Database.spaces get() = this.sequenceOf(SpacesModel)
    private val Database.prvAuth2Layout get() = this.sequenceOf(PrivilegedAuthorToLayoutsModel)

    val space = SpacesModel.aliased("space")

    private val prvAuth2Space = PrivilegedAuthorToSpacesModel.aliased("prvAuth2Space")
    private val layout2Space = LayoutToSpacesModel.aliased("layout2Space")
    private val layout = CompositionLayoutsModel.aliased("layout")
    private val prvAuth2Layout = PrivilegedAuthorToLayoutsModel.aliased("prvAuth2Layout")
    val compSource2Layout = CompositionSourceToLayoutsModel.aliased("compSource2Layout")
    val compSource = CompositionSourcesModel.aliased("compSource")

    // region composition
    val compInstance2compSource = CompositionInstanceToSourcesModel.aliased("compInstance2compSource")
    val compInstance = ImagesCarouselsModel.aliased("compInstance")

    val prvAth2CompSource = PrivilegedAuthorToCompositionSourcesModel.aliased("prvAth2CompSource")
    // endregion

    // region composition's collections
    val img2Col = imageRepository.img2Col
    val img = imageRepository.img
    val text2Col = textRepository.text2Col
    val text = textRepository.text
    val author = AuthorsModel.aliased("author")
    // endregion


    // space and layout
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
    fun getLayoutMetadata(
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
     * Insert new layout.
     *
     * Should always succeed.
     *
     * @param name User defined space.
     * @param authorId Id of author to associate layout to.
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

    fun validateAuthorPrivilegedToModify(layoutId: Int, authorId: Int) =
        database.prvAuth2Layout.find {
            it.authorId eq authorId and
                    (it.layoutId eq layoutId) and
                    (it.modify eq 1)
        } != null

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


    fun getSpace(address: String): ISpaceEntity? {
        return database.spaces.find { it.address eq address }
    }

    fun insertSpace(space: CreateSpaceRequest, address: String): Boolean {
        return database.insert(SpacesModel) {
            set(it.address, address)
        } != 0
    }

    // endregion Insert
}


//    /**
//     * Get layouts of space
//     *
//     * @param spaceAddress
//     */
//    fun getSpaceLayout(spaceAddress: String): ISpaceEntity? {
//        return database.spaces.find { it.address eq spaceAddress }
//    }
// todo - there are 2 different kinds of spaces. Author space (multiple spaces), public space

/**
 * Associate a layout to space
 *
 * @param layoutId Id of composition layout
 * @param spaceAddress Space to associate to by space's address
 */
//fun addLayoutToSpace(layoutId: Int, spaceAddress: String) {
//}

// space own layouts.
// layout owns compositions (components)
//

//    fun getSpacesByAuthor(authorId: Int): Space? {
//        return database.spaces.find { it.authorId eq authorId }
//    }

//    fun softDeleteSpace() {
//    }

//    fun GetThreads(threadsIds: List<Int>): List<Thread> {
//        return emptyList<Thread>()
//    }
//
//    fun GetThreadsByTag(author: String) {
//
//    }
//
//    fun FilterThreadByCategories(threadIds: List<Int>, category: List<String>): Thread {
//        return Thread()
//    }
//
//    fun GetThreadByAuthorsTags(authorId: Int, tag: String): List<Thread> {
//        throw Exception()
//    }
