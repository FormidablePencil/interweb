package com.idealIntent.models.privileges

import models.privileges.IPrivilegedAuthorsToComposition
import models.profile.AuthorsModel
import models.profile.IAuthorEntity
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int

interface IPrivilegedAuthorsToCompositionEntity :
    Entity<IPrivilegedAuthorsToCompositionEntity>, IPrivilegedAuthorsToComposition {
    companion object : Entity.Factory<IPrivilegedAuthorsToCompositionEntity>()

    val author: IAuthorEntity
}

open class PrivilegedAuthorsToCompositionsModel(alias: String?) :
    Table<IPrivilegedAuthorsToCompositionEntity>("privileged_authors_to_compositions", alias) {
    companion object : PrivilegedAuthorsToCompositionsModel(null)

    override fun aliased(alias: String) = PrivilegedAuthorsToCompositionsModel(alias)

    val view = boolean("can_view").bindTo { it.view }
    val modify = boolean("can_modify").bindTo { it.modify }
//    val authorId = int("author_id").bindTo { it.authorId }
    val authorId = int("author_id").references(AuthorsModel) { it.author }
    val privilegeId = int("privilege_id").bindTo { it.privilegeId }
}
