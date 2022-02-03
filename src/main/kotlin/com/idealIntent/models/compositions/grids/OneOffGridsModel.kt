package com.idealIntent.models.compositions.grids

import com.idealIntent.models.compositions.basicCollections.images.D2ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ID2ImageCollectionEntity
import models.IWithName
import com.idealIntent.models.privileges.IPrivilegeSourceEntity
import com.idealIntent.models.privileges.PrivilegeSourcesModel
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * SpaceResponseFailed Grid composition. Composes of 2 dimensional array of images [(collection of image collections)][models.generic.ID2ImageCollectionEntity].
 *
 * @property id Primary key. Referenced by [id][models.compositions.ICompositionLayoutEntity.id] of [layout][models.compositions.ICompositionLayoutEntity].
 */
interface IOneOffGridEntity : Entity<IOneOffGridEntity>, IWithName {
    companion object : Entity.Factory<IOneOffGridEntity>()

    val id: Int
    val privilegesId: IPrivilegeSourceEntity
    val d2ImageCollections: ID2ImageCollectionEntity
}

object OneOffGridsModel : Table<IOneOffGridEntity>("one_off_grids") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val privilegesId = int("privileges_id").references(PrivilegeSourcesModel) { it.privilegesId }
    val d2ImageCollectionsId = int("d2_image_collection_id").references(D2ImageCollectionsModel) { it.d2ImageCollections }
}