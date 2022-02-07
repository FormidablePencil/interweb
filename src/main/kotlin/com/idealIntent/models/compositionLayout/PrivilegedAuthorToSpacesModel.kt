package com.idealIntent.models.compositionLayout

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IPrivilegedAuthorToSpace {
    val authorId: Int
    val spaceId: Int
}

interface IPrivilegedAuthorToSpaceEntity : Entity<IPrivilegedAuthorToSpaceEntity>, IPrivilegedAuthorToSpace {
    companion object : Entity.Factory<IPrivilegedAuthorToSpaceEntity>()
}

open class PrivilegedAuthorToSpacesModel(alias: String?) :
    Table<IPrivilegedAuthorToSpaceEntity>("privileged_author_to_spaces", alias) {
    companion object : PrivilegedAuthorToSpacesModel(null)

    override fun aliased(alias: String) = PrivilegedAuthorToSpacesModel(alias)

    val authorId = int("author_id").bindTo { it.authorId }
    val spaceId = int("space_id").bindTo { it.spaceId }
}
