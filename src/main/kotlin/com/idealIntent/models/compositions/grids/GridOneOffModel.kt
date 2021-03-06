package com.idealIntent.models.compositions.grids

import com.idealIntent.models.compositions.basicCollections.images.ID2ImageCollectionEntity
import models.IWithName
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

/**
 * SpaceResponseFailed Grid composition. Composes of 2 dimensional array of images [(collection of imageUrl collections)][models.generic.ID2ImageCollectionEntity].
 *
 * @property id Primary key. Referenced by [id][models.compositions.ICompositionLayoutEntity.id] of [layout][models.compositions.ICompositionLayoutEntity].
 */
interface IOneOffGridEntity : Entity<IOneOffGridEntity>, IWithName {
    companion object : Entity.Factory<IOneOffGridEntity>()

    val id: Int
    val titlesOfImageCategoriesCollectionId: Int
    val d2ImageCollectionId: Int
    val d2RedirectOnClickCollectionId: Int
    val d2ImgDescriptionsCollectionId: Int
}

// todo - give one_off_grids collection ids foreign keys to their respective tables and do the same for the d2 tables
open class GridOneOffModel(alias: String?) : Table<IOneOffGridEntity>("one_off_grids", alias) {
    companion object : GridOneOffModel(null)

    override fun aliased(alias: String) = GridOneOffModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val titlesOfImageCategoriesCollectionId =
        int("titles_of_image_categories_collection_id").bindTo { it.titlesOfImageCategoriesCollectionId }
    val d2ImageCollectionId = int("d2_image_collection_id").bindTo { it.d2ImageCollectionId }
    val d2RedirectOnClickCollectionId = int("d2_redirect_onclick_collection_id").bindTo { it.d2RedirectOnClickCollectionId }
    val d2ImgDescriptionsCollectionId = int("d2_img_description_collection_id").bindTo { it.d2ImgDescriptionsCollectionId }
}