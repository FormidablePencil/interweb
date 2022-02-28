package com.idealIntent.repositories.compositions

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionCode.CollectionOfRecordsNotFound
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

abstract class ComplexCompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared,
        CreateComposition, DtoMapper, TopLvlIds, Model>(
    override val compInstance: Model,
    private val compInstanceId: Column<Int>
) : SimpleCompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared, CreateComposition,
        DtoMapper, Model>(compInstance, compInstanceId), IComplexCompositionRepositoryStructure<TopLvlIds> {

    override val compositionOnlyIdsSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.privilegeLevel,
        compInstance2compSource.compositionId, compInstance2compSource.sourceId,
        prvAth2CompSource.sourceId, prvAth2CompSource.authorId,
    )

    override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(compInstance as BaseTable<*>, compInstanceId eq compInstance2compSource.compositionId)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
    }
}

abstract class SimpleCompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared,
        CreateComposition, DtoMapper, Model>(open val compInstance: Model, private val compInstanceId: Column<Int>) :
    RepositoryBase(), ICompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared,
        CreateComposition, DtoMapper> {

    val compSource = CompositionSourceRepository.compSource
    val compSource2Layout = CompositionSourceRepository.compSource2Layout
    val prvAth2CompSource = CompositionSourceRepository.prvAth2CompSource
    val compInstance2compSource = CompositionSourceRepository.compInstance2compSource
    val author = AuthorProfileRelatedRepository.author


    /**
     * Left join all related records of composition.
     *
     * Left joins all tables that compose a composition of category and type. For instance, if a composition is a basic imageUrl carousel,
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
    open fun compositionLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compSource2Layout, compSource2Layout.sourceId eq compSource.id)

            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(compInstance as BaseTable<*>, compInstanceId eq compInstance2compSource.compositionId)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
            .leftJoin(author, author.id eq prvAth2CompSource.authorId)
    }

    /**
     * Composition select statement for query.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository]
     * to query layouts of compositions.
     */
    open val compositionSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.id, compSource.privilegeLevel,
        compSource2Layout.orderRank,
        compInstance2compSource.sourceId,
        prvAth2CompSource.authorId, prvAth2CompSource.modify,
        prvAth2CompSource.deletion, prvAth2CompSource.modifyUserPrivileges,
        author.username
    )
}

private interface IComplexCompositionRepositoryStructure<TopLvlIds> {
    // region Composition query instructions
    /**
     * Composition select statement of only ids.
     *
     * @see SimpleCompositionRepositoryStructure.compositionSelect
     */
    val compositionOnlyIdsSelect: MutableList<Column<out Any>>

    /**
     * Left join all related ids of records composed of composition.
     *
     * @see SimpleCompositionRepositoryStructure.compositionLeftJoin
     */
    fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource

    /**
     * Composition's where clause for to query its records properly.
     *
     * E.g. The carousel of images composition with imageUrl and texts representing clickable images to redirect the user
     * to a different page need to be queried together so a where clause of imageUrl order rank is equal to text order rank.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository] to query layouts of compositions.
     *
     * @param mutableList List that will be processed by ktorm to a query as a list of where clauses to apply to the query.
     */
    fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>)


    /**
     * Get only top lvl ids of composition that are privileged to [authorId] to modify.
     *
     * Method [ICompositionRepositoryStructure.deleteComposition] uses it.
     *
     * @param compositionSourceId Id of composition source.
     * @param authorId Criteria query composition of only privileged author.
     * @return Only top level ids of composition.
     */
    fun getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
        compositionSourceId: Int,
        authorId: Int
    ): TopLvlIds?
}

private interface ICompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared,
        CreateComposition, DtoMapper> {
    val database: Database

    // region Composition query instructions
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
     * Composes composition by saving ids of collections and compositions under one composition record.
     * Then associating newly created composition to a composition source which keeps the uniqueness of id.
     *
     * Think of privilege source as a door with a lock required to get through
     * to get your data.
     *
     * @param composePrepared All collection and compositions of ids to compose as one composition.
     * @return Id of newly created composition or throw.
     */
    fun compose(composePrepared: ComposedPrepared, sourceId: Int): Int


    /**
     * Associate composition to source. Used only by [compose].
     */
    fun associateCompToSource(compositionCategory: Int, compositionType: Int, compositionId: Int, sourceId: Int) {
        if (database.insert(CompositionInstanceToSourcesModel) {
                set(it.compositionCategory, compositionCategory)
                set(it.compositionType, compositionType)
                set(it.sourceId, sourceId)
                set(it.compositionId, compositionId)
            } == 0) throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)
    }

    /**
     * Delete composition.
     *
     * Invokes [getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify][IComplexCompositionRepositoryStructure.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify]
     * to query all the ids of collection and compositions
     * composed of composition requested to delete then calls delete methods of its respective repositories to
     * delete each collection and composition composed of composition requested to delete.
     *
     * This method handles one exception which is [when collection by id of composition was not found][CollectionOfRecordsNotFound].
     * This could only mean that composition is corrupt because the composition is composed of compositions
     * and collections that do exist.
     *
     * @param compositionSourceId Id of composition source and not id of composition itself.
     * @param authorId Author's id to validate if privileged to delete.
     * @return success or fail.
     * @throws CompositionException [ CompositionNotFound][CompositionCode.CompositionNotFound].
     */
    fun deleteComposition(compositionSourceId: Int, authorId: Int)

    /**
     * Throw a report if authorId is null. Saves from code duplications. Exception must not be
     * caught. It's a failure of the developer to ensure that authorId is provided when composition
     * restriction is false. Used by private method getCompositionsQuery.
     *
     * @return authorId
     */
    fun throwIfNull(authorId: Int?): Int {
        if (authorId == null)
            throw CompositionExceptionReport(CompositionCode.NoAuthorIdProvidedToRestrictedResource, this::class.java)
        else return authorId
    }
}