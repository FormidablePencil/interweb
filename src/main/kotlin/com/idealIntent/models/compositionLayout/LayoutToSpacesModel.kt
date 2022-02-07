package com.idealIntent.models.compositionLayout

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ILayoutToSpace {
    val spaceAddress: String
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
    val spaceAddress = varchar("space_address").bindTo { it.spaceAddress }
}