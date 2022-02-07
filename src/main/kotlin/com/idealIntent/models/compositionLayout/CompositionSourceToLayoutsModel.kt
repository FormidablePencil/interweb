package com.idealIntent.models.compositionLayout

import models.compositionLayout.ICompositionSourceToLayout
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface ICompositionSourceToLayoutEntity : Entity<ICompositionSourceToLayoutEntity>, ICompositionSourceToLayout {
    companion object : Entity.Factory<ICompositionSourceToLayoutEntity>()

    val layoutId: Int
}

open class CompositionSourceToLayoutsModel(alias: String?) :
    Table<ICompositionSourceToLayoutEntity>("composition_source_to_layouts", alias) {
    companion object : CompositionSourceToLayoutsModel(null)

    override fun aliased(alias: String) = CompositionSourceToLayoutsModel(alias)

    val layoutId = int("layout_id").bindTo { it.layoutId }
    val sourceId = int("source_id").bindTo { it.sourceId }
    val orderRank = int("order_rank").bindTo { it.orderRank }
}
