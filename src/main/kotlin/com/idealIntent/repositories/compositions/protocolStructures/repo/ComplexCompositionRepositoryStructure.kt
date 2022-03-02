package com.idealIntent.repositories.compositions.protocolStructures.repo

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import org.ktorm.dsl.QuerySource
import org.ktorm.dsl.eq
import org.ktorm.dsl.leftJoin
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

abstract class ComplexCompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared,
        CreateComposition, DtoMapper, TopLvlIds, Model>(
    override val compInstance: Model,
    private val compInstanceId: Column<Int>
) : SimpleCompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared, CreateComposition,
        DtoMapper, Model>(compInstance, compInstanceId) {

    /**
     * Composition select statement of only ids.
     *
     * @see SimpleCompositionRepositoryStructure.compositionSelect
     */
    protected open val compositionOnlyIdsSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.privilegeLevel,
        compInstance2compSource.compositionId, compInstance2compSource.sourceId,
        prvAth2CompSource.sourceId, prvAth2CompSource.authorId,
    )

    /**
     * Get only top lvl ids of composition that are privileged to [authorId] to modify.
     *
     * Method [ICompositionRepositoryStructure.deleteComposition] uses it.
     *
     * By default, it throws an exception when attempted to use without the inherited class overriding this method.
     *
     * @param compositionSourceId Id of composition source.
     * @param authorId Criteria query composition of only privileged author.
     * @return Only top level ids of composition.
     */
    protected open fun getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
        compositionSourceId: Int,
        authorId: Int
    ): TopLvlIds? {
        throw CompositionExceptionReport(CompositionCode.MethodNotNeededThusShouldNotBeCalled, this::class.java)
    }


    /**
     * Left join all related ids of records composed of composition.
     *
     * @see SimpleCompositionRepositoryStructure.compositionLeftJoin
     */
    protected open fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(compInstance as BaseTable<*>, compInstanceId eq compInstance2compSource.compositionId)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
    }

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
    protected open fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        throw CompositionExceptionReport(CompositionCode.MethodNotNeededThusShouldNotBeCalled, this::class.java)
    }
}