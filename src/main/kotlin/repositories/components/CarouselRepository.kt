package repositories.components

import dtos.libOfComps.carousels.CarouselBasicImages
import dtos.libOfComps.carousels.CarouselBlurredOverlay
import dtos.libOfComps.genericStructures.Image
import dtos.libOfComps.genericStructures.PrivilegedAuthor
import dtos.libOfComps.genericStructures.Text
import models.genericStructures.*
import models.libOfComps.carousels.ImagesCarousels
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class CarouselRepository(
    private val textRepository: TextRepository,
    private val imageRepository: ImageRepository,
    private val privilegeRepository: PrivilegeRepository
) : RepositoryBase() {
    private val Database.imagesCarousels get() = this.sequenceOf(ImagesCarousels)
    // region CarouselOfImages

    fun insertCarouselBasicImages(component: CarouselBasicImages): Int? {

        val imageCollectionId = imageRepository.insertCollectionOfImages(
            component.images, "CarouselBasicImages component"
        )
        val navToTextCollectionId = textRepository.insertCollectionOfTexts(
            component.navToCorrespondingImagesOrder, "carouselNavLinks"
        )
        val privilegeId = privilegeRepository.insertPrivileges(
            component.privilegedAuthors, "carousel of images"
        )

        // todo - validate ids, println(ids)

        return database.insertAndGenerateKey(ImagesCarousels) {
            set(it.imageCollectionId, imageCollectionId)
            set(it.navToTextCollectionId, navToTextCollectionId)
            set(it.privilegeId, privilegeId)
            set(it.title, component.title)
        } as Int?
    }

    fun getCarouselBasicImagesById(id: Int): CarouselBasicImages {
        // region aliases
        val crslImg = ImagesCarousels.aliased("crlsImg")

        val imgCol = ImageCollections.aliased("imgCol")
        val img = Images.aliased("img")

        val navToCol = TextCollections.aliased("navToCol")
        val navTo = Texts.aliased("navTo")

        val privCol = Privileges.aliased("privCol")
        val priv = PrivilegedAuthors.aliased("priv")
        // endregion

        var title = ""
        val images = mutableListOf<Image>()
        val navTos = mutableListOf<Text>()
        val privilegedAuthors = mutableListOf<PrivilegedAuthor>()

        database.from(crslImg)
            // region join
            .leftJoin(imgCol, on = imgCol.id eq crslImg.imageCollectionId)
            .leftJoin(img, on = img.collectionId eq imgCol.id)

            .leftJoin(navToCol, on = navToCol.id eq crslImg.navToTextCollectionId)
            .leftJoin(navTo, on = navTo.collectionId eq navToCol.id)

            .leftJoin(privCol, on = privCol.id eq crslImg.navToTextCollectionId)
            .leftJoin(priv, on = priv.privilegeId eq privCol.id)
            // endregion

            //region select
            .select(
                crslImg.title,

                imgCol.collectionOf,
                img.orderRank, img.imageTitle, img.imageUrl,

                navToCol.collectionOf,
                navTo.orderRank, navTo.text,

                privCol.privilegesTo,
                priv.authorId, priv.modLvl
            ).where { crslImg.id eq id }
            // endregion
            .map { row ->
                title = row[crslImg.title]!!

                // region images
                println(row[imgCol.collectionOf])
                images.add(
                    Image(
                        orderRank = row[img.orderRank]!!,
                        imageTitle = row[img.imageTitle]!!,
                        imageUrl = row[img.imageUrl]!!
                    )
                )
                // endregion

                // region navTos
                navTos.add(
                    Text(
                        orderRank = row[navTo.orderRank]!!,
                        text = row[navTo.text]!!
                    )
                )
                // endregion

                // region privileges
                privilegedAuthors.add(
                    PrivilegedAuthor(
                        authorId = row[priv.authorId]!!,
                        modLvl = row[priv.modLvl]!! // todo - might fail. error handling
                    )
                )
                // endregion
            }

        return CarouselBasicImages(
            title = title, // todo - may not work
            images = images,
            navToCorrespondingImagesOrder = navTos,
            privilegedAuthors = privilegedAuthors
        )
    }

    fun deleteCarouselOfImagesById(id: Int): Boolean {
        return database.imagesCarousels.removeIf { it.id eq id } != 0
    }

// endregion CarouselOfImages

// region CarouselBlurredOverlay

    fun createCarouselBlurredOverlay(component: CarouselBlurredOverlay): Boolean {
        TODO("Not yet implemented. carousel of images and 1 text box over it")
    }

// endregion CarouselBlurredOverlay
}
