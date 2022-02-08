package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes

/**
 * Carousel of images data
 *
 * @property idAndNameOfCompositions composition instance id, source id, name of composition
 */
class CarouselOfImagesDataMapped {
    val idAndNameOfCompositions = mutableSetOf<Triple<Int, Int, String>>()
    val images = mutableListOf<Pair<Int, ImagePK>>()
    val imgOnclickRedirects = mutableListOf<Pair<Int, TextPK>>()
    val privilegedAuthors = mutableListOf<Pair<Int, PrivilegedAuthor>>()

    fun get(): List<CarouselBasicImagesRes> {
        return idAndNameOfCompositions.map { idAndNameOfComposition ->
            val (compId, sourceId, name) = idAndNameOfComposition

            val compImages = mutableListOf<ImagePK>()
            images.forEach { if (it.first == compId) compImages.add(it.second) }

            val compRedirects = mutableListOf<TextPK>()
            imgOnclickRedirects.forEach { if (it.first == compId) compRedirects.add(it.second) }

            val compPrivilegedAuthors = mutableListOf<PrivilegedAuthor>()
            privilegedAuthors.forEach { if (it.first == compId) compPrivilegedAuthors.add(it.second) }

            CarouselBasicImagesRes(
                id = compId,
                sourceId = sourceId,
                name = name,
                images = compImages,
                imgOnclickRedirects = compRedirects,
                privilegedAuthors = compPrivilegedAuthors
            )
        }
    }
}