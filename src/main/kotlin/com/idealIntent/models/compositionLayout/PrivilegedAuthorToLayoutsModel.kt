package com.idealIntent.models.compositionLayout

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IPrivilegedAuthorToLayouts {
    val authorId: Int
    val layoutId: Int
}

interface IPrivilegedAuthorToLayoutsEntity : Entity<IPrivilegedAuthorToLayoutsEntity>, IPrivilegedAuthorToLayouts {
    companion object : Entity.Factory<IPrivilegedAuthorToLayoutsEntity>()
}

open class PrivilegedAuthorToLayoutsModel(alias: String?) :
    Table<IPrivilegedAuthorToLayoutsEntity>("privileged_author_to_layouts", alias) {
    companion object : PrivilegedAuthorToLayoutsModel(null)

    override fun aliased(alias: String) = PrivilegedAuthorToLayoutsModel(alias)

    val authorId = int("author_id").bindTo { it.authorId }
    val layoutId = int("layout_id").bindTo { it.layoutId }
}
