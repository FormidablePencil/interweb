package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.ImagesCarouselTopLvlIds
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.models.compositionLayout.CompositionLayoutsModel
import com.idealIntent.models.compositionLayout.CompositionSourceToLayoutsModel
import com.idealIntent.models.compositionLayout.LayoutToSpacesModel
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import com.idealIntent.models.privileges.CompositionSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorToCompositionSourcesModel
import com.idealIntent.models.space.SpacesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import models.profile.AuthorsModel
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

data class CarouselOfImagesComposePrepared(
    val imageCollectionId: Int,
    val redirectTextCollectionId: Int,
    val sourceId: Int,
    val name: String,
)

/**
 * Carousel of images repository - responsible for carousel_of_images CRUD actions.
 *
 * Check out [getSingleCompositionOfPrivilegedAuthor] to see what the composition consists of.
 */
class CarouselOfImagesRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    // todo replace Image
) : RepositoryBase(), ICompositionRepositoryStructure<CarouselBasicImagesRes, IImagesCarouselEntity,
        CarouselBasicImagesRes, CarouselOfImagesComposePrepared, CarouselOfImagesDataMapped> {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarouselsModel)

    // todo - move to a source file
    private val space = SpacesModel.aliased("space")
    private val layout2Space = LayoutToSpacesModel.aliased("layout2Space")
    private val layout = CompositionLayoutsModel.aliased("layout")
    private val compSource2Layout = CompositionSourceToLayoutsModel.aliased("compSource2Layout")
    private val compSource = CompositionSourcesModel.aliased("compSource")

    private val compInstance2compSource = CompositionInstanceToSourcesModel.aliased("compInstance2compSource")
    private val compInstance = ImagesCarouselsModel.aliased("compInstance")

    private val prvAth2CompSource = PrivilegedAuthorToCompositionSourcesModel.aliased("prvAth2CompSource")


    // region composition's collections
    val img2Col = ImageToCollectionsModel.aliased("img2Col")
    val img = ImagesModel.aliased("img")
    val text2Col = TextToCollectionsModel.aliased("textRedirect2Col")
    val text = TextsModel.aliased("textRedirect")
    val author = AuthorsModel.aliased("author")
    // endregion


    // region Composition query instructions
    override val compositionSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.id,
        compInstance.id,
        compInstance2compSource.sourceId,
        img2Col.orderRank, img.id, img.url, img.description,
        text2Col.orderRank, text.id, text.text,
        prvAth2CompSource.modify, prvAth2CompSource.deletion,
        author.username
    )

    override val compositionOnlyIdsSelect = mutableListOf<Column<out Any>>(
        compSource.name, compSource.id,
        compInstance2compSource.compositionId,
        compInstance2compSource.sourceId,
        prvAth2CompSource.sourceId, prvAth2CompSource.authorId,
        img.id, text.id,
    )

    override fun compositionLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(compInstance, compInstance2compSource.sourceId eq compSource.id)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
            .leftJoin(author, author.id eq prvAth2CompSource.authorId)

            .leftJoin(img2Col, img2Col.collectionId eq compInstance.imageCollectionId)
            .leftJoin(img, img.id eq img2Col.imageId)

            .leftJoin(text2Col, text2Col.collectionId eq compInstance.redirectTextCollectionId)
            .leftJoin(text, text.id eq text2Col.textId)
    }

    override fun compositionOnlyIdsLeftJoin(querySource: QuerySource): QuerySource {
        return querySource
            .leftJoin(compInstance2compSource, compInstance2compSource.sourceId eq compSource.id)
            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
            .leftJoin(img2Col, img2Col.collectionId eq compInstance.imageCollectionId)
            .leftJoin(text2Col, text2Col.collectionId eq compInstance.redirectTextCollectionId)
    }

    override fun compositionWhereClause(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        mutableList += (img2Col.orderRank eq text2Col.orderRank)
    }

    override fun compositionQueryMap(row: QueryRowSet, dto: CarouselOfImagesDataMapped) {
        println(
            "${row[compSource.name]}," +
                    " ${row[img.id]}," +
                    " ${row[img2Col.orderRank]}," +
                    " ${row[img.url]}," +
                    " ${row[img.description]}," +
                    " ${row[author.username]}, ${row[compSource.id]}"
        )

        dto.idAndNameOfCompositions += Triple(
            row[compInstance.id]!!,
            row[compSource.id]!!,
            row[compSource.name]!!
        )
        dto.images.add(
            Pair(
                row[compInstance.id]!!,
                ImagePK(
                    id = row[text.id]!!,
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
                    view = row[prvAth2CompSource.deletion]!!,
                )
            )
        )
    }
    // endregion


    // region Get top lvl only of composition
    override fun getOnlyTopLvlIdsOfCompositionOnlyModifiable(
        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
    ) = getOnlyTopLvlIdsOfCompositionQuery(onlyModifiable, compositionSourceId, authorId)

    private fun getOnlyTopLvlIdsOfCompositionQuery(
        onlyModifiable: Boolean, compositionSourceId: Int, authorId: Int
    ): ImagesCarouselTopLvlIds? {
        val dto = CarouselOfImagesDataMapped()
        var imageCollectionId: Int? = null
        var textCollectionId: Int? = null

        return compositionOnlyIdsLeftJoin(database.from(compSource))
            .select(compositionOnlyIdsSelect)
            .whereWithConditions {
                (compSource.id eq compositionSourceId) and (text2Col.orderRank eq img2Col.orderRank)
                it += (prvAth2CompSource.authorId eq authorId)
                if (onlyModifiable)
                    it += (prvAth2CompSource.modify eq 1)
            }
            .map {
                compositionQueryMap(it, dto)
                ImagesCarouselTopLvlIds(
                    id = it[compInstance.id]!!,
                    sourceId = it[compSource.id]!!,
                    name = it[compSource.name]!!,
                    imageCollectionId = it[img2Col.collectionId]!!,
                    redirectTextCollectionId = it[text2Col.collectionId]!!
                )
            }.ifEmpty { null }?.first()
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
    ): List<CarouselBasicImagesRes>? {
        val dto = CarouselOfImagesDataMapped()

        compositionLeftJoin(database.from(compSource))
            .select(compositionSelect)
            .whereWithConditions {
                it += (img2Col.orderRank eq text2Col.orderRank)
                it += (compSource.id eq compositionSourceId)
                if (restricted) {
                    val authorIdRes = throwIfNull(authorId)
                    it += (prvAth2CompSource.authorId eq authorIdRes)
                }
            }.map { compositionQueryMap(it, dto) }

        return dto.get().ifEmpty { null }
    }
    // endregion

    override fun compose(composePrepared: CarouselOfImagesComposePrepared): Int? {
        val compositionId = database.insertAndGenerateKey(ImagesCarouselsModel) {
            set(it.imageCollectionId, composePrepared.imageCollectionId)
            set(it.redirectTextCollectionId, composePrepared.redirectTextCollectionId)
        } as Int?
        database.insert(CompositionInstanceToSourcesModel) {
            set(it.compositionCategory, CompositionCategory.Carousel.value)
            set(it.compositionType, CompositionCarouselType.BasicImages.value)
            set(it.sourceId, composePrepared.sourceId)
            set(it.compositionId, compositionId)
        }
        return compositionId
    }

    fun updateName(name: String) {
        database.update(ImagesCarouselsModel) {it}
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int) {
        database.useTransaction {

            val (sourceId, id, name, imageCollectionId, redirectTextCollectionId) = getOnlyTopLvlIdsOfCompositionQuery(
                true,
                compositionSourceId = compositionSourceId,
                authorId = authorId
            ) ?: throw CompositionException(CompositionCode.CompositionNotFound)

            // region deletion
            imageRepository.deleteAllRecordsInCollection(imageCollectionId)
            textRepository.deleteAllRecordsInCollection(redirectTextCollectionId)

            // todo delete items of collections by calling their respective repositories
            //  and compositionSource

//            database.delete(ImagesCarouselsModel) { item -> item.id eq it.id }
//
//            dto.imgOnclickRedirects.forEach { record ->
//                textRepository.deleteTextRecords(record.id)
//            }
//
//            dto.images.forEach { record ->
//                deleteCollectionsUtil.deleteImageRecords(record.id)
//            }
//
//            dto.privilegedAuthors.forEach { record ->
//                if (record.username.isEmpty()) return@forEach
//                val authorId = authorRepository.getByUsername(record.username)?.id
//                    ?: throw Exception("Did not find author by username of ${record.username}")
//
//                deleteCollectionsUtil.deletePrivilegedAuthors(authorId)
//            }
//            database.delete(CompositionSourcesModel) { record -> record.id eq record.privilegeLevel }
            // endregion
        }
    }
}
