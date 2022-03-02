package com.idealIntent.repositories.compositions.protocolStructures.repo

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import org.ktorm.dsl.QuerySource
import org.ktorm.dsl.eq
import org.ktorm.dsl.leftJoin
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column

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

    /**
     * Throw a report if authorId is null. Saves from code duplications. Exception must not be
     * caught. It's a failure of the developer to ensure that authorId is provided when composition
     * restriction is false. Used by private method getCompositionsQuery.
     *
     * @return authorId
     */
    protected fun throwIfNull(authorId: Int?): Int {
        if (authorId == null)
            throw CompositionExceptionReport(CompositionCode.NoAuthorIdProvidedToRestrictedResource, this::class.java)
        else return authorId
    }
}