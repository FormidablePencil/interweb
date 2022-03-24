package com.idealIntent.repositories.compositions.grids

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.grids.GridItem
import com.idealIntent.dtos.compositions.grids.GridOneOffRes3
import com.idealIntent.repositories.compositions.carousels.CompositionMetadata
import com.idealIntent.repositories.compositions.headers.IDataMapper

//data class CompIdAndComp<T>(
//    val sourceId: Int,
//    val collectionId: Int,
//    val content: T,
//)

class GridOneOffDataMapped : IDataMapper<GridOneOffRes3> {
    val compositionsMetadata = mutableSetOf<CompositionMetadata>()
    val privilegedAuthor = mutableSetOf<PrivilegedAuthor>()
    val gridItems = mutableSetOf<GridItem>()

//    val collectionOf_titles_of_image_categories_ofComps = mutableSetOf<CompIdAndComp<String>>()
//    val collectionOf_images_2d_ofComps = mutableSetOf<CompIdAndComp<MutableList<ImagePK>>>()
//    val collectionOf_img_descriptions_ofComps = mutableSetOf<CompIdAndComp<MutableList<TextPK>>>()
//    val collectionOf_onclick_redirects_ofComps = mutableSetOf<CompIdAndComp<MutableList<TextPK>>>()

    override fun get(): List<GridOneOffRes3> {
        return compositionsMetadata.map {
            return@map GridOneOffRes3(
                id = it.compositionId,
                sourceId = it.sourceId,
                gridItems = gridItems.toList(),
                privilegeLevel = 0, // todo
                name = it.name,
                privilegedAuthors = privilegedAuthor.toList(),
            )

            // region deprecated
//            val collectionOf_titles_of_image_categories = collectionOf_titles_of_image_categories_ofComps.find { item ->
//                item.sourceId == it.sourceId
//            }?.content ?: throw Exception("") // todo
//
//            val collectionOf_images_2d = collectionOf_images_2d_ofComps.find { item ->
//                item.sourceId == it.sourceId
//            }?.content ?: throw Exception("")
//
//            val collectionOf_img_descriptions = collectionOf_img_descriptions_ofComps.find { item ->
//                item.sourceId == it.sourceId
//            }?.content ?: throw Exception("")
//
//            val collectionOf_onclick_redirects = collectionOf_onclick_redirects_ofComps.find { item ->
//                item.sourceId == it.sourceId
//            }?.content ?: throw Exception("")
//
//            gridItems += GridItem(
//                title = collectionOf_titles_of_image_categories,
//                images_2d = collectionOf_images_2d,
//                img_descriptions = collectionOf_img_descriptions,
//                onclick_redirects = collectionOf_onclick_redirects,
//            )
            // endregion
        }
    }
}