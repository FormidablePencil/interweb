package com.idealIntent.repositories.compositions.grids

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.grids.GridItem
import com.idealIntent.dtos.compositions.grids.GridOneOffComposePrepared
import com.idealIntent.dtos.compositions.grids.GridOneOffRes3
import com.idealIntent.dtos.compositions.grids.GridOneOffTopLvlIds
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.managers.compositions.images.D2ImageRepository
import com.idealIntent.managers.compositions.texts.D2TextRepository
import com.idealIntent.models.compositions.basicCollections.images.ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.grids.GridOneOffModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.repositories.compositions.carousels.CompositionMetadata
import com.idealIntent.repositories.compositions.protocolStructures.repo.ComplexCompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

class GridOneOffRepository(
    private val d2ImageRepository: D2ImageRepository,
    private val d2TextRepository: D2TextRepository,
) : ComplexCompositionRepositoryStructure<GridOneOffRes3, IImagesCarouselEntity,
        GridOneOffComposePrepared, HeaderBasicRes, GridOneOffDataMapped, GridOneOffTopLvlIds, GridOneOffModel>(
    compInstance = GridOneOffModel,
    compInstanceId = GridOneOffModel.id,
) {
    private val textCol_onclickRedirects = TextCollectionsModel.aliased("textCol_onclickRedirects")
    private val text2Col_onclickRedirects = TextToCollectionsModel.aliased("text2Col_onclickRedirects")
    private val text_onclickRedirects = TextsModel.aliased("text_onclickRedirects")

    private val textCol_imgDescription = TextCollectionsModel.aliased("textCol_imgDescription")
    private val text2Col_imgDescription = TextToCollectionsModel.aliased("text2Col_imgDescription")
    private val text_imgDescription = TextsModel.aliased("text_imgDescription")

    private val textCol_title = TextCollectionsModel.aliased("textCol_title")
    private val text2Col_title = TextToCollectionsModel.aliased("text2Col_title")
    private val text_title = TextsModel.aliased("text_title")

    private val imgCol = ImageCollectionsModel.aliased("imgCol")
    private val img2Col = ImageToCollectionsModel.aliased("img2Col")
    private val img = ImagesModel.aliased("img")

    private val d2ImgCol = D2ImageRepository.d2ImgCol
    private val imgCol2ImgD2Col = D2ImageRepository.imgCol2ImgD2Col
    private val d2TextCol = D2TextRepository.d2TextCol
    private val textCol2TextD2Col = D2TextRepository.textCol2TextD2Col

    // region Reusable query instructions
    override val compositionSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.id, compSource.privilegeLevel,
        compSource2Layout.orderRank,
        compInstance.id, compInstance.d2ImageCollectionId,
        compInstance2compSource.sourceId,

        prvAth2CompSource.authorId, prvAth2CompSource.modify,
        prvAth2CompSource.deletion, prvAth2CompSource.modifyUserPrivileges,
        author.username,

        d2TextCol.id,
        textCol2TextD2Col.d2CollectionId, textCol2TextD2Col.collectionId,
        textCol_title.id,
        text2Col_title.collectionId, text2Col_title.textId, text2Col_title.orderRank,
        text_title.id, text_title.text,
//
//        textCol_onclickRedirects.id,
//        text2Col_onclickRedirects.collectionId, text2Col_onclickRedirects.textId, text2Col_onclickRedirects.orderRank,
//        text_onclickRedirects.id, text_onclickRedirects.text,
//
//        textCol_imgDescription.id,
//        text2Col_imgDescription.collectionId, text2Col_imgDescription.textId, text2Col_imgDescription.orderRank,
//        text_imgDescription.id, text_imgDescription.text,
//
//        d2ImgCol.id,
//        imgCol2ImgD2Col.d2CollectionId, imgCol2ImgD2Col.collectionId,
//        imgCol.id,
//        img2Col.collectionId, img2Col.imageId, img2Col.orderRank,
//        img.id, img.url, img.description,
    )

    override val compositionOnlyIdsSelect = mutableListOf<Column<out Any>>(
        d2ImgCol.id, d2TextCol.id,
        imgCol2ImgD2Col.d2CollectionId, imgCol2ImgD2Col.collectionId,
        textCol2TextD2Col.d2CollectionId, textCol2TextD2Col.collectionId,
        compSource.name, compSource.privilegeLevel,
        compInstance2compSource.compositionId, compInstance2compSource.sourceId,
        prvAth2CompSource.sourceId, prvAth2CompSource.authorId,
    )

    override fun compositionLeftJoin(querySource: QuerySource): QuerySource {
        return super.compositionLeftJoin(querySource)
            .leftJoin(
                d2TextCol, d2TextCol.id eq compInstance.d2ImgDescriptionsCollectionId
                        or (d2TextCol.id eq compInstance.d2RedirectOnClickCollectionId)
                        or (d2TextCol.id eq compInstance.titlesOfImageCategoriesCollectionId)
            )
            .leftJoin(textCol2TextD2Col, textCol2TextD2Col.d2CollectionId eq d2TextCol.id)


            // Titles of each d2 collection
            .leftJoin(textCol_title, textCol_title.id eq compInstance.titlesOfImageCategoriesCollectionId)
            .leftJoin(text2Col_title, text2Col_title.collectionId eq textCol_title.id)
            .leftJoin(text_title, text_title.id eq text2Col_title.collectionId)

//            // Redirections
//            .leftJoin(
//                textCol_onclickRedirects,
//                textCol_onclickRedirects.id eq textCol2TextD2Col.d2CollectionId
//                        and (textCol2TextD2Col.collectionId eq compInstance.d2RedirectOnClickCollectionId)
//            )
//            .leftJoin(text2Col_onclickRedirects, text2Col_onclickRedirects.collectionId eq textCol_onclickRedirects.id)
//            .leftJoin(text_onclickRedirects, text_onclickRedirects.id eq text2Col_onclickRedirects.textId)
//
//            // Image descriptions
//            .leftJoin(
//                textCol_imgDescription,
//                textCol_imgDescription.id eq textCol2TextD2Col.d2CollectionId
//                        and (textCol2TextD2Col.collectionId eq compInstance.d2ImgDescriptionsCollectionId)
//            )
//            .leftJoin(text2Col_imgDescription, text2Col_imgDescription.collectionId eq textCol_imgDescription.id)
//            .leftJoin(text_imgDescription, text_imgDescription.id eq text2Col_imgDescription.textId)
//
//            // Image
//            .leftJoin(d2ImgCol, d2ImgCol.id eq compInstance.d2ImageCollectionId)
//            .leftJoin(imgCol2ImgD2Col, imgCol2ImgD2Col.d2CollectionId eq d2ImgCol.id)
//            .leftJoin(imgCol, imgCol.id eq imgCol2ImgD2Col.collectionId)
//            .leftJoin(img2Col, img2Col.collectionId eq imgCol.id)
//            .leftJoin(img, img.id eq img2Col.imageId)
    }

    override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return super.compositionOnlyIdsLeftJoin(querySource)
            .leftJoin(d2ImgCol, d2ImgCol.id eq compInstance.d2ImageCollectionId)
            .leftJoin(imgCol2ImgD2Col, imgCol2ImgD2Col.d2CollectionId eq d2ImgCol.id)
            .leftJoin(img2Col, img2Col.collectionId eq imgCol2ImgD2Col.collectionId)

            .leftJoin(d2TextCol)
            .leftJoin(
                d2TextCol,
                d2TextCol.id eq compInstance.d2ImgDescriptionsCollectionId
                        and (d2TextCol.id eq compInstance.d2RedirectOnClickCollectionId)
                        and (d2TextCol.id eq compInstance.titlesOfImageCategoriesCollectionId)
            )
            .leftJoin(textCol2TextD2Col, textCol2TextD2Col.d2CollectionId eq d2TextCol.id)
            .leftJoin(
                text2Col_onclickRedirects,
                text2Col_onclickRedirects.collectionId eq textCol2TextD2Col.collectionId
            )
    }

    override fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        mutableList += (img2Col.orderRank eq text2Col_onclickRedirects.orderRank)
    }

    /**
     * Create a grid item for each title of collection. Then by title of collection push content to grid item.
     * Also pushes privileged authors of composition.
     *
     * @see com.idealIntent.repositories.compositions.protocolStructures.repo.ICompositionRepositoryStructure
     */
    override fun compositionQueryMap(row: QueryRowSet, dto: GridOneOffDataMapped) {
////        d2TextCol.id,
////        textCol2TextD2Col.d2CollectionId, textCol2TextD2Col.collectionId,
////        textCol_title.id,
////        text2Col_title.collectionId, text2Col_title.textId, text2Col_title.orderRank,
////        text_title.id, text_title.text,
//
//
        println(row[d2TextCol.id])
        println(row[textCol2TextD2Col.d2CollectionId])
//        println(row[textCol_onclickRedirects.id])
//
//        dto.compositionsMetadata += CompositionMetadata(
//            name = row[compSource.name]!!,
//            orderRank = row[compSource2Layout.orderRank]!!,
//            compositionId = row[compInstance.id]!!,
//            sourceId = row[compSource.id]!!,
//        )
//
//        // region If a grid item was not created, create it
//        if (row[text2Col_onclickRedirects.collectionId] == row[compInstance.titlesOfImageCategoriesCollectionId]
//            && row[text_onclickRedirects.id] == row[text2Col_onclickRedirects.textId]
//        ) {
//            if (dto.gridItems.find { it.title == row[text_onclickRedirects.text]!! } == null) // check if already created
//                dto.gridItems += GridItem(
////                    title = row[text_title.text]!!,
//                    title = "",
//                    images_2d = mutableListOf(),
//                    img_descriptions = mutableListOf(),
//                    onclick_redirects = mutableListOf(),
//                )
//        }
//        // endregion
//
//        // region push items to gridItems
//        val gridItem = dto.gridItems.find {
//            it.title == row[text_onclickRedirects.text]!!
//        }
//        if (gridItem != null) {
//            dto.gridItems.forEach { item ->
//
//                item.images_2d += ImagePK(
//                    id = row[img.id]!!,
//                    orderRank = row[img2Col.orderRank]!!,
//                    url = row[img.url]!!,
//                    description = row[img.description]!!,
//                )
//
//                item.img_descriptions += TextPK(
//                    id = row[text_imgDescription.id]!!,
//                    orderRank = row[text2Col_imgDescription.orderRank]!!,
//                    text = row[text_imgDescription.text]!!,
//                )
//
//                item.onclick_redirects += TextPK(
//                    id = row[text_onclickRedirects.id]!!,
//                    orderRank = row[text2Col_onclickRedirects.orderRank]!!,
//                    text = row[text_onclickRedirects.text]!!,
//                )
//            }
//        }
//        // endregion
//
//        dto.privilegedAuthor += PrivilegedAuthor(
//            username = row[author.username]!!,
//            modify = row[prvAth2CompSource.modify]!!,
//            deletion = row[prvAth2CompSource.deletion]!!,
//            modifyUserPrivileges = row[prvAth2CompSource.modifyUserPrivileges]!!,
//        )
    }


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
    ): List<GridOneOffRes3>? {
        val dto = GridOneOffDataMapped()

        compositionLeftJoin(database.from(compSource))
            .select(compositionSelect)
            .whereWithConditions {
//                it += (img2Col.orderRank eq text2Col_onclickRedirects.orderRank)
                it += (compSource.id eq compositionSourceId)
//                it += (compSource.privilegeLevel eq if (restricted) 1 else 0)
//                if (restricted) {
//                    val authorIdRes = throwIfNull(authorId)
//                    it += (prvAth2CompSource.authorId eq authorIdRes)
//                }
            }.map {
                compositionQueryMap(it, dto)
            }

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