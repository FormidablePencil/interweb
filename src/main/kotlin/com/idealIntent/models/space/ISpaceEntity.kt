package com.idealIntent.models.space

import models.space.ISpace
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ISpaceEntity : Entity<ISpaceEntity>, ISpace {
    companion object : Entity.Factory<ISpaceEntity>()
}

object SpacesModel : Table<ISpaceEntity>("spaces") {
    val address = varchar("address").bindTo { it.address }
    val authorId = int("author_id").bindTo { it.authorId }
    // todo - delete jsonData
    val jsonData = varchar("json_data").bindTo { it.jsonData }
    val created = datetime("created").bindTo { it.created }
}
