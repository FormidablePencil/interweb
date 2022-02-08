package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
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
import com.idealIntent.models.privileges.PrivilegedAuthorsToCompositionSourcesModel
import com.idealIntent.models.space.SpacesModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionRepositoryStructure
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarousel
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

    val space = SpacesModel.aliased("space")
    val layout2Space = LayoutToSpacesModel.aliased("layout2Space")
    val layout = CompositionLayoutsModel.aliased("layout")
    val compSource2Layout = CompositionSourceToLayoutsModel.aliased("compSource2Layout")
    val compSource = CompositionSourcesModel.aliased("compSource")

    // region composition
    val compInstance2compSource = CompositionInstanceToSourcesModel.aliased("compInstance2compSource")
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


    // region Composition query instructions
    override val compositionSelect = listOf<Column<out Any>>(
        compSource.name, compSource.id,
        compInstance.id,
        compInstance2compSource.sourceId,
        img2Col.orderRank, img.id, img.url, img.description,
        text2Col.orderRank, text.id, text.text,
        prvAth2CompSource.modify, prvAth2CompSource.view,
        author.username
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
                    view = row[prvAth2CompSource.view]!!,
                )
            )
        )
    }
    // endregion


    // region Get compositions
    override fun getPublicComposition(compositionSourceId: Int) =
        getCompositionsQuery(restricted = false, authorId = null, compositionSourceId = compositionSourceId).first()

    override fun getPrivateComposition(compositionSourceId: Int, authorId: Int) =
        getCompositionsQuery(restricted = true, authorId = authorId, compositionSourceId = compositionSourceId).first()

    private fun getCompositionsQuery(
        restricted: Boolean, authorId: Int?, compositionSourceId: Int,
    ): List<CarouselBasicImagesRes> {
        val idAndNameOfCompositions = mutableSetOf<Triple<Int, Int, String>>()
        val images = mutableListOf<Pair<Int, ImagePK>>()
        val imgOnclickRedirects = mutableListOf<Pair<Int, TextPK>>()
        val privilegedAuthors = mutableListOf<Pair<Int, PrivilegedAuthor>>()

        val dto = CarouselOfImagesDataMapped()

        database.from(compSource)
            .select(compositionSelect)
            .whereWithConditions {
                it += (img2Col.orderRank eq text2Col.orderRank)
                it += (compSource.id eq compositionSourceId)
                if (restricted) {
                    val authorIdRes = throwIfNull(authorId)
                    it += (prvAth2CompSource.authorId eq authorIdRes)
                }
            }.map { compositionQueryMap(it, dto) }
        return dto.get()
    }
    // endregion


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
//            set(it.name, composePrepared.name) // todo moved name to compSource
            set(it.imageCollectionId, composePrepared.imageCollectionId)
            set(it.redirectTextCollectionId, composePrepared.redirectTextCollectionId)
        } as Int?
        database.insert(CompositionInstanceToSourcesModel) {
            set(it.compositionCategory, CompositionCategory.Carousel.value)
            set(it.compositionType, CompositionCarousel.BasicImages.value)
            set(it.sourceId, composePrepared.sourceId)
            set(it.compositionId, compositionId)
        }
        return compositionId
    }

    override fun getMetadataOfComposition(id: Int): IImagesCarouselEntity? {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(compositionSourceId: Int, authorId: Int): Boolean {
        TODO("Not yet implemented")
    }
}
