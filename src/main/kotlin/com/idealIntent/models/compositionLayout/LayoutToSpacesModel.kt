package com.idealIntent.models.compositionLayout

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ILayoutToSpace {
    val spaceId: Int
    val layoutId: Int
}

interface ILayoutToSpaceEntity : Entity<ILayoutToSpaceEntity>, ILayoutToSpace {
    companion object : Entity.Factory<ILayoutToSpaceEntity>()

    val id: Int
}

open class LayoutToSpacesModel(alias: String?) : Table<ILayoutToSpaceEntity>("layout_to_space", alias) {
    companion object : LayoutToSpacesModel(null)

    override fun aliased(alias: String) = LayoutToSpacesModel(alias)

    val layoutId = int("layout_id").bindTo { it.layoutId }
//    val spaceId = int("space_id").bindTo { it.spaceId }
}