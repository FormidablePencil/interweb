package com.idealIntent.repositories.compositions.grids

import com.idealIntent.dtos.compositions.grids.GridOneOffComposePrepared
import com.idealIntent.dtos.compositions.grids.GridOneOffRes
import com.idealIntent.dtos.compositions.grids.GridOneOffTopLvlIds
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.compositions.images.D2ImageRepository
import com.idealIntent.managers.compositions.texts.D2TextRepository
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.grids.GridOneOffModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.protocolStructures.repo.ComplexCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

class GridOneOffRepository(
    private val d2ImageRepository: D2ImageRepository,
    private val d2TextRepository: D2TextRepository,
) : ComplexCompositionRepositoryStructure<GridOneOffRes, IImagesCarouselEntity,
        GridOneOffComposePrepared, HeaderBasicRes, GridOneOffDataMapped, GridOneOffTopLvlIds, GridOneOffModel>(
    compInstance = GridOneOffModel,
    compInstanceId = GridOneOffModel.id,
) {

    private val text = TextRepository.text
    private val text2Col = TextRepository.text2Col
    private val img = ImageRepository.img
    private val img2Col = ImageRepository.img2Col


    // region Reusable query instructions
    override val compositionSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.id, compSource.privilegeLevel,
        compSource2Layout.orderRank,
        compInstance.id,
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
        TODO("Not yet implemented")
    }

    override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        TODO("Not yet implemented")
    }

    override fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        TODO("Not yet implemented")
    }

    // TODO
    override fun compositionQueryMap(row: QueryRowSet, dto: GridOneOffDataMapped) {
        TODO()
//        dto.compositionsMetadata += CompositionMetadata(
//            name = row[compSource.name]!!,
//            orderRank = row[compSource2Layout.orderRank]!!,
//            compositionId = row[compInstance.id]!!,
//            sourceId = row[compSource.id]!!,
//        )
//
////        dto.images += Pair(
////            row[compInstance.id]!!,
////            ImagePK(
////                id = row[img.id]!!,
////                orderRank = row[img2Col.orderRank]!!,
////                url = row[img.url]!!,
////                description = row[img.description]!!
////            )
////        )
//
//        dto.privilegedAuthors += Pair(
//            row[compInstance.id]!!,
//            PrivilegedAuthor(
//                username = row[author.username]!!,
//                modify = row[prvAth2CompSource.modify]!!,
//                deletion = row[prvAth2CompSource.deletion]!!,
//                modifyUserPrivileges = row[prvAth2CompSource.modifyUserPrivileges]!!,
//            )
//        )
    }
    // endregion


    // region Get top lvl only of composition
    public override fun getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(compositionSourceId: Int, authorId: Int) =
        getOnlyTopLvlIdsOfCompositionQuery(onlyModifiable = true, compositionSourceId, authorId)

    private fun getOnlyTopLvlIdsOfCompositionQuery(
        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
    ): GridOneOffTopLvlIds? = compositionOnlyIdsLeftJoin(database.from(compSource))
        .select(compositionOnlyIdsSelect)
        .whereWithConditions {
            it += (compSource.id eq compositionSourceId)
            it += (prvAth2CompSource.authorId eq authorId)
            if (onlyModifiable)
                it += (prvAth2CompSource.modify eq 1)
        }
        .map {
            TODO()
//            println(
//                "${it[compInstance2compSource.compositionId]} ${it[compInstance2compSource.sourceId]} " +
//                        "${it[compSource.name]} ${it[imgCol.id]} ${it[textCol.id]}"
//            )
//            GridOneOffTopLvlIds(
//                collectionOf_titles_of_image_categories_id =
//                collectionOf_images_2d_id
//                collectionOf_img_descriptions_id
//                collectionOf_onclick_redirects_id
//                privilegesId
//                id = it[compInstance2compSource.compositionId]!!,
//                sourceId = it[compInstance2compSource.sourceId]!!,
//                name = it[compSource.name]!!,
////                collectionId = it[imgCol.id]!!,
////                redirectTextCollectionId = it[textCol.id]!!
//            )
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
    ): List<GridOneOffRes>? {
        val dto = GridOneOffDataMapped()

        compositionLeftJoin(database.from(compSource))
            .select(compositionSelect)
            .whereWithConditions {
                // todo - connect all by order rank of items
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


    override fun compose(composePrepared: GridOneOffComposePrepared, sourceId: Int): Int {
        database.useTransaction {
            val compositionId = database.insertAndGenerateKey(compInstance) {
                set(it.titlesOfImageCategoriesCollectionId, composePrepared.collectionOf_titles_of_image_categories_id)
                set(it.d2ImageCollectionId, composePrepared.collectionOf_images_2d_id)
                set(it.d2RedirectOnClickCollectionId, composePrepared.collectionOf_onclick_redirects_id)
                set(it.d2ImgDescriptionsCollectionId, composePrepared.collectionOf_img_descriptions_id)
            } as Int? ?: throw CompositionExceptionReport(
                CompositionCode.FailedToCompose, this::class.java
            )

            if (database.insert(CompositionInstanceToSourcesModel) {
                    set(it.compositionCategory, CompositionCategory.Carousel.value)
                    set(it.compositionType, CompositionCarouselType.BasicImages.value)
                    set(it.sourceId, sourceId)
                    set(it.compositionId, compositionId)
                } == 0) throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)

            return compositionId
        }
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        try {
            database.useTransaction {
                val data = getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId,
                    authorId = authorId
                ) ?: throw CompositionException(CompositionCode.CompositionNotFound)

                // region deletion
                with(data) {
                    database.delete(compInstance) { it.id eq id }
                    d2TextRepository.deleteRecordsCollection(collectionOf_titles_of_image_categories_id)
                    d2ImageRepository.deleteRecordsCollection(collectionOf_images_2d_id)
                    d2TextRepository.deleteRecordsCollection(collectionOf_img_descriptions_id)
                    d2TextRepository.deleteRecordsCollection(collectionOf_onclick_redirects_id)
                    database.delete(prvAth2CompSource) { it.sourceId eq compositionSourceId }
                    database.delete(compInstance2compSource) { it.sourceId eq compositionSourceId }
                    database.delete(compSource) { it.id eq sourceId }
                }
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