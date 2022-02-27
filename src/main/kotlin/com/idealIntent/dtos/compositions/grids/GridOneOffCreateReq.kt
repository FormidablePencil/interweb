package com.idealIntent.dtos.compositions.grids

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
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
//    val image: String,
//    val navTo: String,
//)

@Serializable
data class GridOneOffCreateReq(
    val collectionOf_titles_of_image_categories: List<Text>,
    val collectionOf_images_2d: List<Pair<List<Image>, Int>>,
    val collectionOf_img_descriptions: List<Pair<List<Text>, Int>>,
    val collectionOf_onclick_redirects: List<Pair<List<Text>, Int>>,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class GridOneOffRes(
    val id: Int,
    val sourceId: Int,
    val collectionOf_titles_of_image_categories: List<Text>,
    val collectionOf_images_2d: List<Pair<List<Image>, Int>>,
    val collectionOf_img_descriptions: List<Pair<List<Text>, Int>>,
    val collectionOf_onclick_redirects: List<Pair<List<Text>, Int>>,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
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
    val collectionOf_titles_of_image_categories_id: Int,
    val collectionOf_images_2d_id: Int,
    val collectionOf_img_descriptions_id: Int,
    val collectionOf_onclick_redirects_id: Int,
)