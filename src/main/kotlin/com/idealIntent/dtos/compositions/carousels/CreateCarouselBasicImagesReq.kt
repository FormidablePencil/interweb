package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.models.compositions.carousels.IImagesCarousel
import dtos.compositions.carousels.ICarouselBasicImages
import kotlinx.serialization.Serializable
import models.IWithPK
import models.IWithPrivilegeSourcePK

// todo - 2 are extra. Required a Refactor.

/**
 * CarouselBasicImage request and response.
 *
 * @property name User provided name.
 * @property images collection of images.
 * @property imgOnclickRedirects collection of redirect text links for when user clicks on image.
 * @property privilegedAuthors privileges.
 */
@Serializable
data class CreateCarouselBasicImagesReq(
    override val name: String,
    override val images: List<Image>,
    override val imgOnclickRedirects: List<Text>,
    override val privilegedAuthors: List<PrivilegedAuthor>,
//    val clickable: boolean,
) : ICarouselBasicImages

//typealias CarouselBasicImagesRes = CarouselBasicImagesRes

@Serializable
data class CarouselBasicImagesRes(
    override val id: Int,
    override val sourceId: Int,
    override val name: String,
    override val images: List<ImagePK>,
    override val imgOnclickRedirects: List<TextPK>,

    override val privilegedAuthors: List<PrivilegedAuthor>,
//    val clickable: boolean,
) : ICarouselBasicImages, IWithPK, IWithPrivilegeSourcePK

// used for query purposes. todo - may need to move
data class ImagesCarouselTopLvlIds(
    val sourceId: Int,
    override val id: Int, // todo - rename to collectionId
    override val name: String,
    override val imageCollectionId: Int,
    override val redirectTextCollectionId: Int
) : IImagesCarousel

//data class CarouselItem( // todo - delete
//    val name: String,
//    val url: String,
//    val imageAlt: String,
//    val navigateTo: String,
//)


//data class ShortClip(
//    val name: String,
//    val shortClip: String,
//    val thumbnail: String,
//)
//
//data class CarouselCard(
//    val name: String,
//    val readmeLink: String,
//    val contentFromGithub: String,
//    val previewImage: String,
//)
//
//data class CarouselDoubleAwesome(
//    val shortClips: List<ShortClip>,
//    val cards: List<CarouselCard>,
//)
