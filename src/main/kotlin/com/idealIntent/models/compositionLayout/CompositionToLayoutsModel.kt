package com.idealIntent.models.compositionLayout

import models.compositionLayout.ICompositionLayoutItem
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ICompositionLayoutItemEntity : Entity<ICompositionLayoutItemEntity>, ICompositionLayoutItem {
    companion object : Entity.Factory<ICompositionLayoutItemEntity>()
}

open class CompositionToLayoutsModel(alias: String?) :
    Table<ICompositionLayoutItemEntity>("composition_layout_items", alias) {
    companion object : CompositionToLayoutsModel(null)

    override fun aliased(alias: String) = CompositionToLayoutsModel(alias)

    val layoutId = int("id").bindTo { it.layoutId }
    val compositionId = int("composition_id").bindTo { it.compositionId } // todo - is multiple references possible?
    val orderRank = int("order_rank").bindTo { it.orderRank }
    val compositionType = int("composition_type").bindTo { it.compositionType }
}
