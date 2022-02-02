package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesReq
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.models.compositions.carousels.IImagesCarouselEntity
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.privileges.PrivilegedAuthorsToCompositionsModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.ICompositionRepoStructure
import models.profile.AuthorsModel
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf

data class CarouselOfImagesComposePrepared(
    val imageCollectionId: Int,
    val redirectTextCollectionId: Int,
    val privilegeId: Int,
    val name: String,
)

// todo - get ICollectionStructure implemented or create a new one just for Composition lvl repositories
class CarouselOfImagesRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    // todo replace Image
) : RepositoryBase(), ICompositionRepoStructure<CarouselBasicImagesReq, IImagesCarouselEntity,
        CarouselBasicImagesReq, CarouselOfImagesComposePrepared> {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarouselsModel)

    // region Get
    override fun getComposition(id: Int): CarouselBasicImagesReq? {
        val comp = ImagesCarouselsModel.aliased("comp")
        val prvAth = PrivilegedAuthorsToCompositionsModel("prvAuthorsComp")
        val author = AuthorsModel.aliased("author")

        val img2Col = ImageToCollectionsModel.aliased("img2Col")
        val img = ImagesModel.aliased("img")

        val text2Col = TextToCollectionsModel.aliased("textRedirect2Col")
        val text = TextsModel.aliased("textRedirect")

        var name = ""
        val images = mutableListOf<Image>()
        val imgOnclickRedirects = mutableListOf<Text>()
        val privilegedAuthors = mutableListOf<PrivilegedAuthor>()

        database.from(comp)
            .leftJoin(img2Col, img2Col.collectionId eq comp.imageCollectionId)
            .leftJoin(img, img.id eq img2Col.collectionId)

            .leftJoin(text2Col, text2Col.collectionId eq comp.redirectTextCollectionId)
            .leftJoin(text, text.id eq comp.redirectTextCollectionId)

            .leftJoin(prvAth, prvAth.privilegeId eq comp.privilegeId)
            .leftJoin(author, author.id eq prvAth.authorId)

            .select(comp.name,
                img2Col.orderRank, img.id, img.url, img.description,
                text2Col.orderRank, text.text,
                prvAth.modify, prvAth.view,
                author.username)
            .where { comp.id eq id }
            .map {
                name = it[comp.name]!!
                images.add(
                    Image(
                        id = it[img.id],
                        orderRank = it[img2Col.orderRank]!!,
                        url = it[img.url]!!,
                        description = it[img.description]!!
                    )
                )
                imgOnclickRedirects.add(
                    Text(id = it[img.id]!!, orderRank = it[text2Col.orderRank]!!, text = it[text.text]!!)
                )
                privilegedAuthors.add(
                    PrivilegedAuthor(
                        username = it[author.username]!!,
                        modify = it[prvAth.modify]!!,
                        view = it[prvAth.view]!!
                    )
                )
            }

        return CarouselBasicImagesReq(
            name = name,
            images = images,
            imgOnclickRedirects = imgOnclickRedirects,
            privilegedAuthors = privilegedAuthors
        )
    }

    // endregion Get

    // region Insert
    override fun compose(composePrepared: CarouselOfImagesComposePrepared): Int? {
        return database.insertAndGenerateKey(ImagesCarouselsModel) {
            set(it.name, composePrepared.name)
            set(it.imageCollectionId, composePrepared.imageCollectionId)
            set(it.redirectTextCollectionId, composePrepared.redirectTextCollectionId)
            set(it.privilegeId, composePrepared.privilegeId)
        } as Int?
    }
    // endregion Insert

    override fun getMetadataOfComposition(id: Int): IImagesCarouselEntity? {
        TODO("Not yet implemented")
    }

    override fun deleteComposition(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}
