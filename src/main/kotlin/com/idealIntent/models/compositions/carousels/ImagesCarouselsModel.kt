package com.idealIntent.models.compositions.carousels

import com.idealIntent.models.compositions.basicCollections.images.IImageCollectionEntity
import com.idealIntent.models.compositions.basicCollections.images.ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.ITextCollectionEntity
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import models.privileges.IPrivilegeEntity
import models.privileges.PrivilegesModel
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

/**
 * A carousel composition. Composes of a collection of images and a collection of redirect links which is
 * mapped over images. When an image of carousel is click the user will be redirected to another site.
 */
interface IImagesCarouselEntity : Entity<IImagesCarouselEntity> {
    companion object : Entity.Factory<IImagesCarouselEntity>()

    val privilege: IPrivilegeEntity
    val imageCollection: IImageCollectionEntity
    val redirectTextCollection: ITextCollectionEntity
    val id: Int
}

open class ImagesCarouselsModel(alias: String?) : Table<IImagesCarouselEntity>("image_carousels", alias) {
    companion object : ImagesCarouselsModel(null)

    override fun aliased(alias: String) = ImagesCarouselsModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val imageCollectionId = int("image_collection_id").references(ImageCollectionsModel) { it.imageCollection }
    val redirectTextCollectionId =
        int("redirect_text_collection_id").references(TextCollectionsModel) { it.redirectTextCollection }
    val privilegeId = int("privilege_id").references(PrivilegesModel) { it.privilege }
}