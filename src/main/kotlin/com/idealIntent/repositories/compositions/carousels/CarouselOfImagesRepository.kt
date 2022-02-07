package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.compositionLayout.CompositionLayoutsModel
import com.idealIntent.models.compositionLayout.CompositionSourceToLayoutsModel
import com.idealIntent.models.compositionLayout.LayoutToSpacesModel
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.privileges.CompositionInstanceToSourcesTable
import com.idealIntent.models.privileges.CompositionSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorsToCompositionSourcesModel
import com.idealIntent.models.space.SpacesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionRepositoryStructure
import models.profile.AuthorsModel
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

data class CarouselOfImagesComposePrepared(
    val imageCollectionId: Int,
    val redirectTextCollectionId: Int,
    val sourceId: Int,
    val name: String,
)

class CarouselOfImagesData {
    val idAndNameOfCompositions = mutableSetOf<Triple<Int, Int, String>>()
    val images = mutableListOf<Pair<Int, ImagePK>>()
    val imgOnclickRedirects = mutableListOf<Pair<Int, TextPK>>()
    val privilegedAuthors = mutableListOf<Pair<Int, PrivilegedAuthor>>()
}

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
        CarouselBasicImagesRes, CarouselOfImagesComposePrepared> {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarouselsModel)

     val space = SpacesModel.aliased("space")
     val layout2Space = LayoutToSpacesModel.aliased("layout2Space")
     val layout = CompositionLayoutsModel.aliased("layout")
     val compSource2Layout = CompositionSourceToLayoutsModel.aliased("compSource2Layout")
     val compSource = CompositionSourcesModel.aliased("compSource")

    // region composition
     val compInstance2compSource = CompositionInstanceToSourcesTable.aliased("compInstance2compSource")
     val compInstance = ImagesCarouselsModel.aliased("compInstance")

     val prvAth2CompSource = PrivilegedAuthorsToCompositionSourcesModel.aliased("prvAth2CompSource")
    // endregion

    // region composition's collections
     val img2Col = ImageToCollectionsModel.aliased("img2Col")
     val img = ImagesModel.aliased("img")
     val text2Col = TextToCollectionsModel.aliased("textRedirect2Col")
     val text = TextsModel.aliased("textRedirect")
     val author = AuthorsModel.aliased("author")
    // endregion

    val selectCarouselOfImages = listOf<Column<out Any>>(
        compInstance.name, compInstance.id,
        img2Col.orderRank, img.id, img.url, img.description,
        text2Col.orderRank, text.id, text.text,
        prvAth2CompSource.modify, prvAth2CompSource.view,
        author.username
    )

    fun whereClauseCarouselOfImages(mutableList: MutableList<ColumnDeclaring<Boolean>>) {
        mutableList += (img2Col.orderRank eq text2Col.orderRank)
//        it += (compInstance.id eq compositionId)
    }

    fun mapClauseBuilderCarouselOfImages(dto: CarouselOfImagesData): (QueryRowSet) -> Boolean {
        val queryMap = {
                row: QueryRowSet,
            ->
            println(
                "${row[compInstance.name]}," +
                        " ${row[img.id]}," +
                        " ${row[img2Col.orderRank]}," +
                        " ${row[img.url]}," +
                        " ${row[img.description]}," +
                        " ${row[author.username]}, ${row[compSource.id]}"
            )

            dto.idAndNameOfCompositions += Triple(
                row[compInstance.id]!!,
                row[compInstance.sourceId]!!,
                row[compInstance.name]!!
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
                        view = row[prvAth2CompSource.view]!!,
                    )
                )
            )
        }

        return queryMap
    }

    // region Get

    fun getPublicComposition(compositionId: Int) =
        getComposition(restricted = false, layoutId = null, authorId = null, compositionId = compositionId)

    fun getPrivateComposition(compositionId: Int, authorId: Int) =
        getComposition(restricted = true, layoutId = null, authorId = authorId, compositionId = compositionId)

    fun getPublicCompositionOfLayout(layoutId: Int, compositionId: Int, authorId: Int) =
        getComposition(restricted = false, layoutId = layoutId, authorId = authorId, compositionId = compositionId)

    fun getPrivateCompositionOfLayout(layoutId: Int, compositionId: Int, authorId: Int) =
        getComposition(restricted = true, layoutId = layoutId, authorId = authorId, compositionId = compositionId)


    /**
     * Get all the compositions of layout.
     *
     *
     * @param layoutId
     */


    /**
     * Gets all compositions that fulfill the criteria.
     */
    private fun getComposition(
        restricted: Boolean,
        authorId: Int?,
        layoutId: Int?,
        compositionId: Int,
    ): List<CarouselBasicImagesRes> {
        val idAndNameOfCompositions = mutableSetOf<Triple<Int, Int, String>>()
        val images = mutableListOf<Pair<Int, ImagePK>>()
        val imgOnclickRedirects = mutableListOf<Pair<Int, TextPK>>()
        val privilegedAuthors = mutableListOf<Pair<Int, PrivilegedAuthor>>()

        // either select from compositions or don't select at all

        database.from(compInstance)
            .select(selectCarouselOfImages)
            .whereWithConditions {
                it += (img2Col.orderRank eq text2Col.orderRank)
                it += (compInstance.id eq compositionId)
                if (restricted)
                    if (authorId == null)
                        throw CompositionExceptionReport(
                            CompositionCode.NoAuthorIdProvidedToRestrictedResource,
                            this::class.java
                        )
                    else it += (prvAth2CompSource.authorId eq authorId)
            }.map {
                it
            }


        return idAndNameOfCompositions.map { idAndNameOfComposition ->
            val (compId, sourceId, name) = idAndNameOfComposition

            val compImages = mutableListOf<ImagePK>()
            images.forEach { if (it.first == compId) compImages.add(it.second) }

            val compRedirects = mutableListOf<TextPK>()
            images.forEach { if (it.first == compId) compImages.add(it.second) }
            imgOnclickRedirects.forEach { if (it.first == compId) compRedirects.add(it.second) }

            val compPrivilegedAuthors = mutableListOf<PrivilegedAuthor>()
            privilegedAuthors.forEach { if (it.first == compId) compPrivilegedAuthors.add(it.second) }

            return@map CarouselBasicImagesRes(
                id = compId,
                sourceId = sourceId,
                name = name,
                images = compImages,
                imgOnclickRedirects = compRedirects,
                privilegedAuthors = compPrivilegedAuthors
            )
        }
    }

    fun leftJoinCarouselBasicImages(querySource: QuerySource): QuerySource {
        querySource.leftJoin(compInstance, compInstance.sourceId eq compSource.id)

            .leftJoin(prvAth2CompSource, prvAth2CompSource.sourceId eq compSource.id)
            .leftJoin(author, author.id eq prvAth2CompSource.authorId)

            .leftJoin(img2Col, img2Col.collectionId eq compInstance.imageCollectionId)
            .leftJoin(img, img.id eq img2Col.imageId)

            .leftJoin(text2Col, text2Col.collectionId eq compInstance.redirectTextCollectionId)
            .leftJoin(text, text.id eq text2Col.textId)
        return querySource
    }

    /**
     * Get single composition of privileged author. Used for testing purposes.
     */
    override fun getSingleCompositionOfPrivilegedAuthor(
        compositionId: Int,
        authorId: Int
    ): List<CarouselBasicImagesRes> = listOf()

    /**
     * Used for testing purposes.
     */
    override fun getAllCompositionsAssociatedOfAuthor(
        authorId: Int
    ): List<CarouselBasicImagesRes> = listOf()

    /**
     * Get all records by criteria query.
     *
     * @param editable Editable by author compositions.
     * @param deletable Deletable by author compositions.
     * @param compositionId Query by composition's unique id.
     * @param authorId Query all compositions associated to author's id.
     */

    // endregion Get

    // region Insert
    /**
     * Composes composition by saving ids as one record. Then associating newly created composition
     * to a privilege source. Think of privilege source as a door with a lock required to get through
     * to get your data.
     *
     * @param composePrepared
     * @return
     */
    // todo - no need to return Int if compositionSourceId is returned instead I believe
    override fun compose(composePrepared: CarouselOfImagesComposePrepared): Int? {
        val compositionId = database.insertAndGenerateKey(ImagesCarouselsModel) {
            set(it.name, composePrepared.name)
            set(it.imageCollectionId, composePrepared.imageCollectionId)
            set(it.redirectTextCollectionId, composePrepared.redirectTextCollectionId)
        } as Int?
        database.insert(CompositionInstanceToSourcesTable) {
            set(it.sourceId, composePrepared.sourceId)
            set(it.compositionId, compositionId)
        }
        return compositionId
    }
// endregion Insert

    override fun getMetadataOfComposition(id: Int): IImagesCarouselEntity? {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}
