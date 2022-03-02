package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBlurredOverlayComposePrepared
import com.idealIntent.dtos.compositions.carousels.CarouselBlurredOverlayRes
import com.idealIntent.dtos.compositions.carousels.ImagesCarouselTopLvlIds
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositions.carousels.CarouselBlurredOverlayModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.compositions.protocolStructures.repo.ComplexCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import org.ktorm.dsl.*

class CarouselBlurredOverlayRepository(
) : ComplexCompositionRepositoryStructure<CarouselBlurredOverlayRes, IImagesCarouselEntity, CarouselBlurredOverlayComposePrepared,
        CarouselBlurredOverlayRes, CarouselBlurredOverlayDataMapped, ImagesCarouselTopLvlIds, CarouselBlurredOverlayModel>(
    compInstance = CarouselBlurredOverlayModel,
    compInstanceId = CarouselBlurredOverlayModel.id
) {
    // region composition's collections
    private val imgCol = ImageRepository.imgCol
    private val img2Col = ImageRepository.img2Col
    private val img = ImageRepository.img
    // endregion


    // region Reusable query instructions
    init {
        super.compositionSelect += mutableListOf(
            compInstance.id,
            img2Col.orderRank, img.id, img.url, img.description,
        )
        super.compositionOnlyIdsSelect += mutableListOf(
            compInstance.imageCollectionId,
            imgCol.id,
        )
    }

    public override fun compositionLeftJoin(querySource: QuerySource): QuerySource {
        return super.compositionLeftJoin(querySource)
            .leftJoin(img2Col, img2Col.collectionId eq compInstance.imageCollectionId)
            .leftJoin(img, img.id eq img2Col.imageId)
    }

    public override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return super.compositionOnlyIdsLeftJoin(querySource)
            .leftJoin(imgCol, imgCol.id eq compInstance.imageCollectionId)
    }

    // todo - didn't add privilege level. May be needed
    override fun compositionQueryMap(row: QueryRowSet, dto: CarouselBlurredOverlayDataMapped) {
//        println(
//            "${row[compSource.name]}," +
//                    " ${row[img.id]}," +
//                    " ${row[img2Col.orderRank]}," +
//                    " ${row[img.url]}," +
//                    " ${row[img.description]}," +
//                    " ${row[author.username]}, ${row[compSource.id]}"
//        )
//
//        dto.compositionsMetadata += CompositionMetadata(
//            name = row[compSource.name]!!,
//            orderRank = row[compSource2Layout.orderRank]!!,
//            compositionId = row[compInstance.id]!!,
//            sourceId = row[compSource.id]!!,
//        )
//        dto.images.add(
//            Pair(
//                row[compInstance.id]!!,
//                ImagePK(
//                    id = row[img.id]!!,
//                    orderRank = row[img2Col.orderRank]!!,
//                    url = row[img.url]!!,
//                    description = row[img.description]!!
//                )
//            )
//        )
//        dto.imgOnclickRedirects.add(
//            Pair(
//                row[compInstance.id]!!,
//                TextPK(
//                    id = row[text.id]!!,
//                    orderRank = row[text2Col.orderRank]!!,
//                    text = row[text.text]!!
//                )
//            )
//        )
//        dto.privilegedAuthors.add(
//            Pair(
//                row[compInstance.id]!!,
//                PrivilegedAuthor(
//                    username = row[author.username]!!,
//                    modify = row[prvAth2CompSource.modify]!!,
//                    deletion = row[prvAth2CompSource.deletion]!!,
//                    modifyUserPrivileges = row[prvAth2CompSource.modifyUserPrivileges]!!,
//                )
//            )
//        )
    }
    // endregion


    // region Get composition
    override fun getPublicComposition(compositionSourceId: Int) =
        getCompositionsQuery(
            restricted = false,
            authorId = null,
            compositionSourceId = compositionSourceId
        )?.first()

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int) =
        getCompositionsQuery(
            restricted = true,
            authorId = authorId,
            compositionSourceId = compositionSourceId
        )?.first()

    private fun getCompositionsQuery(
        restricted: Boolean, authorId: Int?, compositionSourceId: Int,
    ): List<CarouselBlurredOverlayRes>? {
        val dto = CarouselBlurredOverlayDataMapped()

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
//
        return dto.get().ifEmpty { null }
    }
    // endregion


    override fun compose(composePrepared: CarouselBlurredOverlayComposePrepared, sourceId: Int): Int {
        database.useTransaction {
            val compositionId = database.insertAndGenerateKey(compInstance) {
                set(it.imageCollectionId, composePrepared.imageCollectionId)
                set(it.textCollectionId, composePrepared.textCollectionId)
            } as Int? ?: throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)

            associateCompToSource(
                compositionCategory = CompositionCategory.Carousel.value,
                compositionType = CompositionCarouselType.BasicImages.value,
                compositionId = compositionId, sourceId = sourceId
            )

            return compositionId
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        TODO("Not yet implemented")
    }

}
