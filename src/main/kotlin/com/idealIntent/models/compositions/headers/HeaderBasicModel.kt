package com.idealIntent.models.compositions.headers

import models.IWithPK
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface IHeaderBasicEntity : Entity<IHeaderBasicEntity>, IWithPK {
    companion object : Entity.Factory<IHeaderBasicEntity>()

    val bgImg: String
    val profileImg: String
}

open class HeaderBasicModel(alias: String?) : Table<IHeaderBasicEntity>("basic_headers", alias) {
    companion object : HeaderBasicModel(null)

    override fun aliased(alias: String) = HeaderBasicModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val profileImg = varchar("profile_img").bindTo { it.profileImg }
    val bgImg = varchar("bg_img").bindTo { it.bgImg }
}
