package com.idealIntent.models.compositionLayout

import models.IWithName
import models.IWithPK
import models.IWithPrivilegeSourcePK
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ILayout : IWithName, IWithPrivilegeSourcePK {
    val layoutId: Int
}

interface ICompositionLayoutEntity : Entity<ICompositionLayoutEntity>, ILayout {
    companion object : Entity.Factory<ICompositionLayoutEntity>()
}

open class CompositionLayoutsModel(alias: String?) : Table<ICompositionLayoutEntity>("composition_layouts", alias) {
    companion object : CompositionLayoutsModel(null)

    override fun aliased(alias: String) = CompositionLayoutsModel(alias)

    val id = int("id").primaryKey().bindTo { it.layoutId }
    val name = varchar("name").bindTo { it.name }
}
