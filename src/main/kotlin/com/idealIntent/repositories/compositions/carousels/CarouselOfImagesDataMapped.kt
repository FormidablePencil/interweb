package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes

data class CompositionMetadata(
    val name: String,
    val orderRank: Int,
    val compositionId: Int,
    val sourceId: Int,
)

class CarouselOfImagesDataMapped {
    val compositionsMetadata = mutableSetOf<CompositionMetadata>()
    val images = mutableSetOf<Pair<Int, ImagePK>>()
    val imgOnclickRedirects = mutableSetOf<Pair<Int, TextPK>>()
    val privilegedAuthors = mutableSetOf<Pair<Int, PrivilegedAuthor>>()

    fun get(): List<CarouselBasicImagesRes> {
        return compositionsMetadata.map { item ->
            val (name, orderRank, compositionId, sourceId) = item

            val compImages = mutableListOf<ImagePK>()
            images.forEach { if (it.first == compositionId) compImages.add(it.second) }

            val compRedirects = mutableListOf<TextPK>()
            imgOnclickRedirects.forEach { if (it.first == compositionId) compRedirects.add(it.second) }

            val compPrivilegedAuthors = mutableListOf<PrivilegedAuthor>()
            privilegedAuthors.forEach { if (it.first == compositionId) compPrivilegedAuthors.add(it.second) }

            CarouselBasicImagesRes(
                orderRank = orderRank,
                id = compositionId,
                sourceId = sourceId,
                name = name,
                images = compImages,
                imgOnclickRedirects = compRedirects,
                privilegedAuthors = compPrivilegedAuthors
            )
        }

        // region Deprecated
//        return idAndNameOfCompositions.map { idAndNameOfComposition ->
//            val (compId, sourceId, name) = idAndNameOfComposition
//
//            val compImages = mutableListOf<ImagePK>()
//            images.forEach { if (it.first == compId) compImages.add(it.second) }
//
//            val compRedirects = mutableListOf<TextPK>()
//            imgOnclickRedirects.forEach { if (it.first == compId) compRedirects.add(it.second) }
//
//            val compPrivilegedAuthors = mutableListOf<PrivilegedAuthor>()
//            privilegedAuthors.forEach { if (it.first == compId) compPrivilegedAuthors.add(it.second) }
//
//            CarouselBasicImagesRes(
//                orderRank = 12,
//                id = compId,
//                sourceId = sourceId,
//                name = name,
//                images = compImages,
//                imgOnclickRedirects = compRedirects,
//                privilegedAuthors = compPrivilegedAuthors
//            )
//        }
        // endregion
    }
}