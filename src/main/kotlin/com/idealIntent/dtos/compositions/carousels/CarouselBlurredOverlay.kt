package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import dtos.compositions.carousels.ICarouselBlurredOverlay
import kotlinx.serialization.Serializable

@Serializable
data class CarouselBlurredOverlayCreateReq(
    val info: String,
    val navTo: String,
    val images: List<Image>,
    val texts: List<Text>,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class CarouselBlurredOverlayRes(
    override val info: String,
    override val navTo: String,
    override val previewImages: List<Image>,
    override val title: String
) : ICarouselBlurredOverlay


data class CarouselBlurredOverlayComposePrepared(
    val imageCollectionId: Int,
    val textCollectionId: Int,
)