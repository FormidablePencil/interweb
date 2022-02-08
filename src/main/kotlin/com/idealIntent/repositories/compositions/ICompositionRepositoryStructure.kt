package com.idealIntent.repositories.compositions

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
     * Used in [SpaceRepository][com.idealIntent.repositories.SpaceRepository]
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
     * Used in [SpaceRepository][com.idealIntent.repositories.SpaceRepository] to query layouts of compositions.
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
     * Used in [SpaceRepository][com.idealIntent.repositories.SpaceRepository] to query layouts of compositions.
     *
     * @param mutableList List that will be processed by ktorm to a query as a list of where clauses to apply to the query.
     */
    fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>)

    /**
     * Map clause builder carousel of images
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.SpaceRepository] to query layouts of compositions.
     *
     * @param row Table row.
     * @param dto To save records under.
     */
    fun compositionQueryMap(row: QueryRowSet, dto: DtoMapper)
    // endregion


    // region Get compositions
    /**
     * Get public composition
     *
     * @param compositionSourceId Id of composition source.
     * @return Composition
     */
    fun getPublicComposition(compositionSourceId: Int): ResponseOfComposition?

    /**
     * Get private composition
     *
     * @param compositionSourceId Id of composition source.
     * @param authorId Validate that author is privileged to view composition.
     * @return Composition
     */
    fun getPrivateComposition(compositionSourceId: Int, authorId: Int): ResponseOfComposition?

    /**
     * Get composition for modification
     *
     * @param compositionSourceId Id of composition source.
     * @param authorId Validate that author is privileged to modify.
     * @return Composition
     */
    // endregion


    /**
     * Get only composition's metadata and not it's associated records
     *
     * todo - Implement if a need for this functionality comes.aa
     *
     * @param id ID of composition
     * @return Composition's metadata and not associated records or null if failed to find by [id]
     */
    fun getMetadataOfComposition(id: Int): CompositionMetadata?

    /**
     * Take ids of collections, compositions and privilege source and insert them into composition's foreign key columns.
     *
     * @return Id of the newly created composition.
     */
    fun compose(composePrepared: ComposePrepared): Int?

    /**
     * Delete composition
     *
     * todo - implement
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

//    /** todo - get records by criteria
//     * Get all records by criteria query.
//     *
//     * @param editable Editable by author compositions.
//     * @param deletable Deletable by author compositions.
//     * @param compositionId Query by composition's unique id.
//     * @param authorId Query all compositions associated to author's id.
//     */
