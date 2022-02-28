package com.idealIntent.repositories.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.texts.TextLonelyCreateReq
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.texts.TextLonelyModel
import com.idealIntent.repositories.compositions.SimpleCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.texts.CompositionTextType
import org.ktorm.dsl.*
import org.ktorm.schema.Column

class TextLonelyRepository : SimpleCompositionRepositoryStructure<TextLonelyRes, IImagesCarouselEntity,
        TextLonelyCreateReq, TextLonelyRes, TextLonelyDataMapped, TextLonelyModel>(
    compInstance = TextLonelyModel,
    compInstanceId = TextLonelyModel.id
) {

    // region Reusable query instructions
    init {
        super.compositionSelect += mutableListOf(compInstance.id, compInstance.text)
    }

    override fun compositionQueryMap(row: QueryRowSet, dto: TextLonelyDataMapped) {
        dto.data += Pair(
            row[compInstance.id]!!,
            TextLonelyRes(
                compositionId = row[compInstance.id]!!,
                name = row[compSource.name]!!,
                text = row[compInstance.text]!!,
                privilegedAuthors = listOf(),
            )
        )

        dto.privilegedAuthors += Pair(
            row[compInstance.id]!!,
            PrivilegedAuthor(
                username = row[author.username]!!,
                modify = row[prvAth2CompSource.modify]!!,
                deletion = row[prvAth2CompSource.deletion]!!,
                modifyUserPrivileges = row[prvAth2CompSource.modifyUserPrivileges]!!,
            )
        )
    }
    // endregion


    // region Get composition
    override fun getPublicComposition(compositionSourceId: Int): TextLonelyRes? =
        getCompositionsQuery(
            restricted = false,
            authorId = null,
            compositionSourceId = compositionSourceId
        )?.first()

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): TextLonelyRes? =
        getCompositionsQuery(
            restricted = true,
            authorId = authorId,
            compositionSourceId = compositionSourceId
        )?.first()

    private fun getCompositionsQuery(
        restricted: Boolean, authorId: Int?, compositionSourceId: Int,
    ): List<TextLonelyRes>? {
        val dto = TextLonelyDataMapped()

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

    override fun compose(composePrepared: TextLonelyCreateReq, sourceId: Int): Int {
        database.useTransaction {
            val compositionId = database.insertAndGenerateKey(compInstance) {
                set(it.text, composePrepared.text)
            } as Int? ?: throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)

            associateCompToSource(
                compositionCategory = CompositionCategory.Text.value,
                compositionType = CompositionTextType.Basic.value,
                compositionId = compositionId, sourceId = sourceId
            )

            return compositionId
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        TODO("Not yet implemented")
    }
    // endregion
}