package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.AuthorToPrivilege
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import dtos.compositions.carousels.ICarouselBasicImages
import kotlinx.serialization.Serializable

// todo - 2 are extra. Required a Refactor.
@Serializable
data class CarouselBasicImages(
    override val title: String,
    override val images: List<Image>,
    override val navToCorrespondingImagesOrder: List<Text>,
    override val privilegedAuthors: List<AuthorToPrivilege>,
//    val clickable: boolean,
): ICarouselBasicImages

//data class CarouselItem( // todo - delete
//    val title: String,
//    val url: String,
//    val imageAlt: String,
//    val navigateTo: String,
//)


//data class ShortClip(
//    val title: String,
//    val shortClip: String,
//    val thumbnail: String,
//)
//
//data class CarouselCard(
//    val title: String,
//    val readmeLink: String,
//    val contentFromGithub: String,
//    val previewImage: String,
//)
//
//data class CarouselDoubleAwesome(
//    val shortClips: List<ShortClip>,
//    val cards: List<CarouselCard>,
//)
