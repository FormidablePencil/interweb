package com.idealIntent.repositories.compositions.headers

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.headers.HeaderBasicModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.repositories.compositions.SimpleCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.headers.CompositionHeader
import org.ktorm.dsl.*

class HeaderBasicRepository(
) : SimpleCompositionRepositoryStructure<HeaderBasicRes, IImagesCarouselEntity,
        HeaderBasicCreateReq, HeaderBasicRes, HeaderBasicDataMapped, HeaderBasicModel>(
    compInstance = HeaderBasicModel,
    compInstanceId = HeaderBasicModel.id,
) {

    init {
        super.compositionSelect += mutableListOf(compInstance.id, compInstance.bgImg, compInstance.profileImg)
    }

    // region Reusable query instructions
    override fun compositionQueryMap(row: QueryRowSet, dto: HeaderBasicDataMapped) {
        if (dto.data.isEmpty())
            dto.data += HeaderBasicRes(
                id = row[compInstance.id]!!,
                sourceId = row[compSource.id]!!,
                name = row[compSource.name]!!,
                bgImg = row[compInstance.bgImg]!!,
                profileImg = row[compInstance.profileImg]!!,
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
//    override fun getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//        compositionSourceId: Int,
//        authorId: Int
//    ): HeaderBasicTopLvlIds? =
//        getOnlyTopLvlIdsOfCompositionQuery(onlyModifiable = true, compositionSourceId, authorId)
//
//    private fun getOnlyTopLvlIdsOfCompositionQuery(
//        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
//    ): HeaderBasicTopLvlIds? = compositionOnlyIdsLeftJoin(database.from(compSource))
//        .select(compositionOnlyIdsSelect)
//        .whereWithConditions {
//            it += (compSource.id eq compositionSourceId)
//            it += (prvAth2CompSource.authorId eq authorId)
//            if (onlyModifiable)
//                it += (prvAth2CompSource.modify eq 1)
//        }
//        .map {
//            println(
//                "${it[compInstance2compSource.compositionId]} ${it[compInstance2compSource.sourceId]} " +
//                        "${it[compSource.name]}"
//            )
//            HeaderBasicTopLvlIds(
//                id = it[compInstance2compSource.compositionId]!!,
//                sourceId = it[compInstance2compSource.sourceId]!!,
//                name = it[compSource.name]!!,
//            )
//        }.ifEmpty { null }?.first()
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
                CompositionCode.FailedToCompose, this::class.java
            )

            if (database.insert(CompositionInstanceToSourcesModel) {
                    set(it.compositionCategory, CompositionCategory.Header.value)
                    set(it.compositionType, CompositionHeader.Basic.value)
                    set(it.sourceId, sourceId)
                    set(it.compositionId, compositionId)
                } == 0) throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)

            return compositionId
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        TODO()
//        database.useTransaction {
//            val (sourceId, id, name) = getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
//                compositionSourceId = compositionSourceId,
//                authorId = authorId
//            ) ?: throw CompositionException(CompositionCode.CompositionNotFound)
//
//            // region deletion
//            database.delete(ImagesCarouselsModel) { it.id eq id }
//            database.delete(PrivilegedAuthorToCompositionSourcesModel) { it.sourceId eq compositionSourceId }
//            database.delete(CompositionInstanceToSourcesModel) { it.sourceId eq compositionSourceId }
//            database.delete(CompositionSourcesModel) { it.id eq id }
//            // endregion
//        }
    }
}