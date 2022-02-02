package com.idealIntent.models.space

import models.IWithOrder
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ILayoutsToSpaceEntity : Entity<ILayoutsToSpaceEntity>, IWithOrder {
    companion object : Entity.Factory<ILayoutsToSpaceEntity>()

    val space: ISpaceEntity
    val layout: ILayoutsToSpaceEntity
}

open class LayoutsToSpacesModel(alias: String?) :
    Table<ILayoutsToSpaceEntity>("layouts_to_spaces", alias) {
    companion object : LayoutsToSpacesModel(null)

    override fun aliased(alias: String) = LayoutsToSpacesModel(alias)

    val orderRank = int("order_rank").bindTo { it.orderRank }
    val spaceId = int("space_id").references(SpacesModel) { it.space }
    val layoutId = int("layout_id").references(LayoutsToSpacesModel) { it.layout }
}
