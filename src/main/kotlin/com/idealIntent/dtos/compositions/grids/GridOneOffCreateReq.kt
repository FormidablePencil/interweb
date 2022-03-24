package com.idealIntent.dtos.compositions.grids

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import kotlinx.serialization.Serializable

//data class GridOneOff(
//    val columns: List<GridOneOffColumn>,
//)
//
//data class GridOneOffColumn(
//    val title: String,
//    val items: List<GridOffItem>,
//)
//
//data class GridOffItem(
//    val title: String,
//    val imageUrl: String,
//    val navTo: String,
//)

@Serializable
data class GridOneOffCreateReq(
    val collectionOf_titles_of_image_categories: List<Text>,
    val collectionOf_images_2d: List<Pair<Int, List<Image>>>, // Int is order rank!
    val collectionOf_img_descriptions: List<Pair<Int, List<Text>>>,
    val collectionOf_onclick_redirects: List<Pair<Int, List<Text>>>,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class GridOneOffRes(
    val id: Int,
    val sourceId: Int,
    val collectionOf_titles_of_image_categories: List<Text>,
    val collectionOf_images_2d: List<Pair<Int, List<Image>>>,
    val collectionOf_img_descriptions: List<Pair<Int, List<Text>>>,
    val collectionOf_onclick_redirects: List<Pair<Int, List<Text>>>,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class GridOneOffRes3(
    val id: Int,
    val sourceId: Int,
    val gridItems: List<GridItem>,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class GridItem(
    val title: String,
    val images_2d: MutableList<ImagePK>,
    val img_descriptions: MutableList<TextPK>,
    val onclick_redirects: MutableList<TextPK>,
)

@Serializable
data class GridOneOffTopLvlIds(
    val id: Int,
    val sourceId: Int,
    val collectionOf_titles_of_image_categories_id: Int,
    val collectionOf_images_2d_id: Int,
    val collectionOf_img_descriptions_id: Int,
    val collectionOf_onclick_redirects_id: Int,
)

data class GridOneOffComposePrepared(
    val sourceId: Int,
    val collectionOf_titles_of_image_categories_id: Int,
    val collectionOf_images_2d_id: Int,
    val collectionOf_img_descriptions_id: Int,
    val collectionOf_onclick_redirects_id: Int,
)