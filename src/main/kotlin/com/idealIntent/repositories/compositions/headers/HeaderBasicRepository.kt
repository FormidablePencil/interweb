package com.idealIntent.repositories.compositions.headers

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.dtos.compositions.headers.HeaderBasicTopLvlIds
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositionLayout.CompositionSourceToLayoutsModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.compositions.headers.HeaderBasicModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.models.privileges.CompositionSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorToCompositionSourcesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.compositions.ICompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.headers.CompositionHeader
import models.profile.AuthorsModel
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

class HeaderBasicRepository(
) : RepositoryBase(), ICompositionRepositoryStructure<HeaderBasicRes, IImagesCarouselEntity,
        HeaderBasicCreateReq, HeaderBasicRes, HeaderBasicDataMapped, HeaderBasicTopLvlIds> {

    private val compSource = CompositionSourcesModel.aliased("compSource")
    private val compInstance = HeaderBasicModel.aliased("compInstance")
    private val compSource2Layout = CompositionSourceToLayoutsModel.aliased("compSource2Layout")
    private val compInstance2compSource = CompositionInstanceToSourcesModel.aliased("compInstance2compSource")
    private val author = AuthorsModel.aliased("author")

    // todo - move to privileged source repository
    private val prvAth2CompSource = PrivilegedAuthorToCompositionSourcesModel.aliased("prvAth2CompSource")


    // region Reusable query instructions
    override val compositionSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.id, compSource.privilegeLevel,
        compSource2Layout.orderRank,
        compInstance.id, compInstance.bgImg,
        compInstance2compSource.sourceId,
        prvAth2CompSource.authorId, prvAth2CompSource.modify,
        prvAth2CompSource.deletion, prvAth2CompSource.modifyUserPrivileges,
        author.username
    )

    override val compositionOnlyIdsSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.privilegeLevel,
        compInstance2compSource.compositionId, compInstance2compSource.sourceId,
        prvAth2CompSource.sourceId, prvAth2CompSource.authorId,
    )

    override fun compositionLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compSource2Layout, compSource2Layout.sourceId eq compSource.id)

            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(compInstance, compInstance.id eq compInstance2compSource.compositionId)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
            .leftJoin(author, author.id eq prvAth2CompSource.authorId)
    }

    override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(compInstance, compInstance.id eq compInstance2compSource.compositionId)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
    }

    override fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        TODO("Not yet implemented")
    }

    override fun compositionQueryMap(row: QueryRowSet, dto: HeaderBasicDataMapped) {
        if (dto.data.isNotEmpty())
            dto.data += HeaderBasicRes(
                id = row[compInstance.id]!!,
                sourceId = row[compSource.id]!!,
                name = row[compSource.name]!!,
                bgImg = row[compInstance.bgImg]!!,
                profileImg = row[compInstance.bgImg]!!,
                privilegeLevel = row[compSource.privilegeLevel]!!,
                privilegedAuthors = listOf()
            )

        dto.privilegedAuthors += PrivilegedAuthor(
            username = row[author.username]!!,
            modify = row[prvAth2CompSource.modify]!!,
            deletion = row[prvAth2CompSource.deletion]!!,
            modifyUserPrivileges = row[prvAth2CompSource.modifyUserPrivileges]!!
        )
    }
    // endregion


    // region Get top lvl only of composition
    override fun getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
        compositionSourceId: Int,
        authorId: Int
    ): HeaderBasicTopLvlIds? =
        getOnlyTopLvlIdsOfCompositionQuery(onlyModifiable = true, compositionSourceId, authorId)

    private fun getOnlyTopLvlIdsOfCompositionQuery(
        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
    ): HeaderBasicTopLvlIds? = compositionOnlyIdsLeftJoin(database.from(compSource))
        .select(compositionOnlyIdsSelect)
        .whereWithConditions {
            it += (compSource.id eq compositionSourceId)
            it += (prvAth2CompSource.authorId eq authorId)
            if (onlyModifiable)
                it += (prvAth2CompSource.modify eq 1)
        }
        .map {
            println(
                "${it[compInstance2compSource.compositionId]} ${it[compInstance2compSource.sourceId]} " +
                        "${it[compSource.name]}"
            )
            HeaderBasicTopLvlIds(
                id = it[compInstance2compSource.compositionId]!!,
                sourceId = it[compInstance2compSource.sourceId]!!,
                name = it[compSource.name]!!,
            )
        }.ifEmpty { null }?.first()
    // endregion


    // region Get composition
    override fun getPublicComposition(compositionSourceId: Int): HeaderBasicRes? =
        getCompositionsQuery(
            restricted = false,
            authorId = null,
            compositionSourceId = compositionSourceId
        )?.first()

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): HeaderBasicRes? =
        getCompositionsQuery(
            restricted = true,
            authorId = authorId,
            compositionSourceId = compositionSourceId
        )?.first()

    private fun getCompositionsQuery(
        restricted: Boolean, authorId: Int?, compositionSourceId: Int,
    ): List<HeaderBasicRes>? {
        val dto = HeaderBasicDataMapped()

        compositionLeftJoin(database.from(compSource))
            .select(compositionSelect)
            .whereWithConditions {
                it += (compSource.id eq compositionSourceId)
                it += (compSource.privilegeLevel eq if (restricted) 1 else 0)
                if (restricted) {
                    val authorIdRes = throwIfNull(authorId)
                    it += (prvAth2CompSource.authorId eq authorIdRes)
                }
            }.map { compositionQueryMap(it, dto) }

        return dto.get().ifEmpty { null }
    }
    // endregion


    override fun compose(composePrepared: HeaderBasicCreateReq, sourceId: Int): Int {
        database.useTransaction {
            val compositionId = database.insertAndGenerateKey(HeaderBasicModel) {
                set(it.bgImg, composePrepared.bgImg)
                set(it.profileImg, composePrepared.profileImg)
            } as Int? ?: throw CompositionExceptionReport(
                CompositionCode.FailedToComposeInternalError, this::class.java
            )

            if (database.insert(CompositionInstanceToSourcesModel) {
                    set(it.compositionCategory, CompositionCategory.Header.value)
                    set(it.compositionType, CompositionHeader.Basic.value)
                    set(it.sourceId, sourceId)
                    set(it.compositionId, compositionId)
                } == 0) throw CompositionExceptionReport(CompositionCode.FailedToComposeInternalError, this::class.java)

            return compositionId
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        database.useTransaction {
            val (sourceId, id, name) = getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                compositionSourceId = compositionSourceId,
                authorId = authorId
            ) ?: throw CompositionException(CompositionCode.CompositionNotFound)

            // region deletion
            database.delete(ImagesCarouselsModel) { it.id eq id }
            database.delete(PrivilegedAuthorToCompositionSourcesModel) { it.sourceId eq compositionSourceId }
            database.delete(CompositionInstanceToSourcesModel) { it.sourceId eq compositionSourceId }
            database.delete(CompositionSourcesModel) { it.id eq id }
            // endregion
        }
    }
}