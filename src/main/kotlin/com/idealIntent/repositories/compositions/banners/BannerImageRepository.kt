package com.idealIntent.repositories.compositions.banners

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.banners.BannerBasicCreateReq
import com.idealIntent.dtos.compositions.banners.BannerBasicRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositions.banners.BannerImageModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.compositions.SimpleCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.texts.CompositionTextType
import org.ktorm.dsl.*

class BannerImageRepository : SimpleCompositionRepositoryStructure<BannerBasicRes, IImagesCarouselEntity,
        BannerBasicCreateReq, BannerBasicRes, BannerImageDataMapped, BannerImageModel>(
    compInstance = BannerImageModel,
    compInstanceId = BannerImageModel.id
) {

    init {
       super.compositionSelect += mutableListOf(compInstance.id, compInstance.imageUrl, compInstance.imageAlt)
    }

    // region Reusable query instructions
    override fun compositionQueryMap(row: QueryRowSet, dto: BannerImageDataMapped) {
        dto.data += Pair(
            row[compInstance.id]!!,
            BannerBasicRes(
                compositionId = row[compInstance.id]!!,
                sourceId = row[compSource.id]!!,
                imageUrl = row[compInstance.imageUrl]!!,
                imageAlt = row[compInstance.imageAlt]!!,
                name = row[compSource.name]!!,
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
    override fun getPublicComposition(compositionSourceId: Int): BannerBasicRes? =
        getCompositionsQuery(
            restricted = false,
            authorId = null,
            compositionSourceId = compositionSourceId
        )?.first()

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int): BannerBasicRes? =
        getCompositionsQuery(
            restricted = true,
            authorId = authorId,
            compositionSourceId = compositionSourceId
        )?.first()

    private fun getCompositionsQuery(
        restricted: Boolean, authorId: Int?, compositionSourceId: Int,
    ): List<BannerBasicRes>? {
        val dto = BannerImageDataMapped()

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

    override fun compose(composePrepared: BannerBasicCreateReq, sourceId: Int): Int {
        database.useTransaction {
            val compositionId = database.insertAndGenerateKey(compInstance) {
                set(it.imageUrl, composePrepared.imageUrl)
                set(it.imageAlt, composePrepared.imageAlt)
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