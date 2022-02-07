package com.idealIntent.models.space

import com.idealIntent.models.compositionLayout.LayoutToSpacesModel
import models.IWithPK
import models.space.ISpace
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ISpaceEntity : Entity<ISpaceEntity>, ISpace, IWithPK {
    companion object : Entity.Factory<ISpaceEntity>()
}

open class SpacesModel(alias: String?) : Table<ISpaceEntity>("spaces", alias) {
    companion object : SpacesModel(null)

    override fun aliased(alias: String) = SpacesModel(alias)

    val id = int("id").primaryKey().bindTo { it.id } // todo - make unique
    val address = varchar("address").bindTo { it.address } // todo - make unique
    val authorId = int("author_id").bindTo { it.authorId }

    // todo - delete jsonData
    val jsonData = varchar("json_data").bindTo { it.jsonData }
    val created = datetime("created").bindTo { it.created }
}
