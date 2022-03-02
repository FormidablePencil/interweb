package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.dtos.compositions.carousels.ImagesCarouselTopLvlIds
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.protocolStructures.repo.ComplexCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import org.ktorm.dsl.*
import org.ktorm.schema.ColumnDeclaring

/**
 * Carousel of images repository - responsible for carousel_of_images CRUD actions.
 *
 * Check out [getSingleCompositionOfPrivilegedAuthor] to see what the composition consists of.
 */
class CarouselOfImagesRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
) : ComplexCompositionRepositoryStructure<CarouselBasicImagesRes, IImagesCarouselEntity, CarouselOfImagesComposePrepared,
        CarouselBasicImagesRes, CarouselOfImagesDataMapped, ImagesCarouselTopLvlIds, ImagesCarouselsModel>(
    compInstance = ImagesCarouselsModel,
    compInstanceId = ImagesCarouselsModel.id
) {

    // region composition's collections
    private val imgCol = ImageRepository.imgCol
    private val img2Col = ImageRepository.img2Col
    private val img = ImageRepository.img
    private val textCol = TextRepository.textCol
    private val text2Col = TextRepository.text2Col
    private val text = TextRepository.text
    // endregion


    // region Reusable query instructions
    init {
        super.compositionSelect += mutableListOf(
            compInstance.id,
            img2Col.orderRank, img.id, img.url, img.description,
            text2Col.orderRank, text.id, text.text,
        )
        super.compositionOnlyIdsSelect += mutableListOf(
            compInstance.redirectTextCollectionId, compInstance.imageCollectionId,
            imgCol.id, textCol.id,
        )
    }

    override fun compositionLeftJoin(querySource: QuerySource): QuerySource {
        return super.compositionLeftJoin(querySource)
            .leftJoin(img2Col, img2Col.collectionId eq compInstance.imageCollectionId)
            .leftJoin(img, img.id eq img2Col.imageId)

            .leftJoin(text2Col, text2Col.collectionId eq compInstance.redirectTextCollectionId)
            .leftJoin(text, text.id eq text2Col.textId)
    }

    override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return super.compositionOnlyIdsLeftJoin(querySource)
            .leftJoin(imgCol, imgCol.id eq compInstance.imageCollectionId)
            .leftJoin(textCol, textCol.id eq compInstance.redirectTextCollectionId)
    }

    public override fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        mutableList += (img2Col.orderRank eq text2Col.orderRank)
    }

    // todo - didn't add privilege level. May be needed
    override fun compositionQueryMap(row: QueryRowSet, dto: CarouselOfImagesDataMapped) {
        println(
            "${row[compSource.name]}," +
                    " ${row[img.id]}," +
                    " ${row[img2Col.orderRank]}," +
                    " ${row[img.url]}," +
                    " ${row[img.description]}," +
                    " ${row[author.username]}, ${row[compSource.id]}"
        )

        dto.compositionsMetadata += CompositionMetadata(
            name = row[compSource.name]!!,
            orderRank = row[compSource2Layout.orderRank]!!,
            compositionId = row[compInstance.id]!!,
            sourceId = row[compSource.id]!!,
        )
        dto.images.add(
            Pair(
                row[compInstance.id]!!,
                ImagePK(
                    id = row[img.id]!!,
                    orderRank = row[img2Col.orderRank]!!,
                    url = row[img.url]!!,
                    description = row[img.description]!!
                )
            )
        )
        dto.imgOnclickRedirects.add(
            Pair(
                row[compInstance.id]!!,
                TextPK(
                    id = row[text.id]!!,
                    orderRank = row[text2Col.orderRank]!!,
                    text = row[text.text]!!
                )
            )
        )
        dto.privilegedAuthors.add(
            Pair(
                row[compInstance.id]!!,
                PrivilegedAuthor(
                    username = row[author.username]!!,
                    modify = row[prvAth2CompSource.modify]!!,
                    deletion = row[prvAth2CompSource.deletion]!!,
                    modifyUserPrivileges = row[prvAth2CompSource.modifyUserPrivileges]!!,
                )
            )
        )
    }
    // endregion


    // region Get top lvl only of composition
    public override fun getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(compositionSourceId: Int, authorId: Int) =
        getOnlyTopLvlIdsOfCompositionQuery(onlyModifiable = true, compositionSourceId, authorId)

    private fun getOnlyTopLvlIdsOfCompositionQuery(
        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
    ): ImagesCarouselTopLvlIds? = compositionOnlyIdsLeftJoin(database.from(compSource))
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
                        "${it[compSource.name]} ${it[imgCol.id]} ${it[textCol.id]}"
            )
            ImagesCarouselTopLvlIds(
                id = it[compInstance2compSource.compositionId]!!,
                sourceId = it[compInstance2compSource.sourceId]!!,
                name = it[compSource.name]!!,
                imageCollectionId = it[imgCol.id]!!,
                redirectTextCollectionId = it[textCol.id]!!
            )
        }.ifEmpty { null }?.first()
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
    ): List<CarouselBasicImagesRes>? {
        val dto = CarouselOfImagesDataMapped()

        compositionLeftJoin(database.from(compSource))
            .select(compositionSelect)
            .whereWithConditions {
                it += (img2Col.orderRank eq text2Col.orderRank)
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


    override fun compose(composePrepared: CarouselOfImagesComposePrepared, sourceId: Int): Int {
        database.useTransaction {
            val compositionId = database.insertAndGenerateKey(compInstance) {
                set(it.imageCollectionId, composePrepared.imageCollectionId)
                set(it.redirectTextCollectionId, composePrepared.redirectTextCollectionId)
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
        try {
            database.useTransaction {
                val (sourceId, id, name, imageCollectionId, redirectTextCollectionId) = getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId,
                    authorId = authorId
                ) ?: throw CompositionException(CompositionCode.CompositionNotFound)

                // region deletion
                database.delete(compInstance) { it.id eq id }
                imageRepository.deleteRecordsCollection(imageCollectionId)
                textRepository.deleteRecordsCollection(redirectTextCollectionId)
                database.delete(prvAth2CompSource) { it.sourceId eq compositionSourceId }
                database.delete(compInstance2compSource) { it.sourceId eq compositionSourceId }
                database.delete(compSource) { it.id eq id }
                // endregion
            }
        } catch (ex: CompositionException) {
            when (ex.code) {
                CompositionCode.CollectionOfRecordsNotFound ->
                    throw CompositionExceptionReport(
                        CompositionCode.CompositionRecordIsCorrupt,
                        compositionSourceId.toString(),
                        this::class.java,
                        ex
                    )
                else -> throw CompositionException(ex.code, ex.moreDetails, ex)
            }
        }
    }
}
