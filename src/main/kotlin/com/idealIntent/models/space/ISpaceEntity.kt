package com.idealIntent.models.space

import models.space.ISpace
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar

interface ISpaceEntity : Entity<ISpaceEntity>, ISpace {
    companion object : Entity.Factory<ISpaceEntity>()
}

open class SpacesModel(alias: String?) : Table<ISpaceEntity>("spaces", alias) {
    companion object : SpacesModel(null)

    override fun aliased(alias: String) = SpacesModel(alias)

    val address = varchar("address").primaryKey().bindTo { it.address }
    val created = datetime("created").bindTo { it.created }
}
