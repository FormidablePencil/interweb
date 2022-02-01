package com.idealIntent.models.compositionLayout

import models.IWithName
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ICompositionLayoutEntity : Entity<ICompositionLayoutEntity>, IWithName {
    companion object : Entity.Factory<ICompositionLayoutEntity>()

    val id: Int
}

open class CompositionLayoutsModel(alias: String?) : Table<ICompositionLayoutEntity>("composition_layouts", alias) {
    companion object : CompositionLayoutsModel(null)

    override fun aliased(alias: String) = CompositionLayoutsModel(alias)

    val id = int("id").bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}
