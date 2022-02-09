package com.idealIntent.repositories.compositions

import com.idealIntent.dtos.compositions.carousels.ImagesCarouselTopLvlIds
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.QuerySource
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

interface ICompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, CreateComposition, ComposePrepared, DtoMapper> {

    // region Composition query instructions
    /**
     * Composition select statement for query.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository]
     * to query layouts of compositions.
     */
    val compositionSelect: List<Column<out Any>>

    /**
     * Composition select statement of only ids.
     *
     * @see compositionSelect
     */
    val compositionOnlyIdsSelect: MutableList<Column<out Any>>

    /**
     * Left join all related records of composition.
     *
     * Left joins all tables that compose a composition of category and type. For instance, if a composition is a basic image carousel,
     * it will all join text, text to collection relationship and text collection so all the records of a composition
     * were gotten of a layout.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository] to query layouts of compositions.
     *
     * todo - many compositions will have the same tables to query it's records. At the moment will be duplicate of
     *  table join statements. Maybe add left join statements to a Set and process append the query after all compositions
     *  of layout have added their join statement.
     *
     * @param querySource The query to append left join statements to.
     * @return The appended left join statements for querying records of composition.
     */
    fun compositionLeftJoin(querySource: QuerySource): QuerySource

    /**
     * Left join all related ids of records composed of composition.
     *
     * @see compositionLeftJoin
     */
    fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource

    /**
     * Composition's where clause for to query its records properly.
     *
     * E.g. The carousel of images composition with image and texts representing clickable images to redirect the user
     * to a different page need to be queried together so a where clause of image order rank is equal to text order rank.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository] to query layouts of compositions.
     *
     * @param mutableList List that will be processed by ktorm to a query as a list of where clauses to apply to the query.
     */
    fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>)

    /**
     * Map clause builder carousel of images.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository] to query layouts of compositions.
     *
     * @param row Table row.
     * @param dto To save records under.
     */
    fun compositionQueryMap(row: QueryRowSet, dto: DtoMapper)
    // endregion


    /**
     * Get only top lvl ids of composition that are privileged to [authorId] to modify.
     *
     * Method [deleteComposition] uses it.
     *
     * @param onlyModifiable Criteria query only modifiable composition.
     * @param compositionSourceId Id of composition source.
     * @param authorId Criteria query composition of only privileged author.
     * @return Only top level ids of composition.
     */
    fun getOnlyTopLvlIdsOfCompositionOnlyModifiable(
        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
    ): ImagesCarouselTopLvlIds?


    // region Get compositions
    /**
     * Get public composition.
     *
     * @param compositionSourceId Id of composition source.
     * @return Composition
     */
    fun getPublicComposition(compositionSourceId: Int): ResponseOfComposition?

    /**
     * Get private composition.
     *
     * @param compositionSourceId Id of composition source.
     * @param authorId Validate that author is privileged to view composition.
     * @return Composition
     */
    fun getPrivateComposition(compositionSourceId: Int, authorId: Int): ResponseOfComposition?
    // endregion


    /**
     * Composes composition by saving ids as one record. Then associating newly created composition
     * to a privilege source. Think of privilege source as a door with a lock required to get through
     * to get your data.
     *
     * @param composePrepared All collection and compositions of ids to compose as one composition.
     * @return Id of newly created composition or null if failed.
     */
    fun compose(composePrepared: ComposePrepared): Int?

    /**
     * Delete composition.
     *
     * Invokes [getOnlyTopLvlIdsOfCompositionOnlyModifiable] to query all the ids of collection and compositions
     * composed of composition requested to delete then calls delete methods of its respective repositories to
     * delete each collection and composition composed of composition requested to delete.
     *
     * @param compositionSourceId Id of composition source and not id of composition itself.
     * @param authorId Author's id to validate if privileged to delete.
     * @return success or fail.
     */
    fun deleteComposition(compositionSourceId: Int, authorId: Int): Boolean

    /**
     * Throw if authorId is null. Saves from code duplications.
     *
     * @return authorId
     */
    fun throwIfNull(authorId: Int?): Int {
        if (authorId == null)
            throw CompositionExceptionReport(CompositionCode.NoAuthorIdProvidedToRestrictedResource, this::class.java)
        else return authorId
    }
}