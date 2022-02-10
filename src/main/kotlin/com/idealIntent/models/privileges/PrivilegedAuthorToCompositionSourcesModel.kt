package com.idealIntent.models.privileges

import models.privileges.IPrivilegedAuthorsToComposition
import models.profile.AuthorsModel
import models.profile.IAuthorEntity
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IPrivilegedAuthorToCompositionSourceEntity :
    Entity<IPrivilegedAuthorToCompositionSourceEntity>, IPrivilegedAuthorsToComposition {
    companion object : Entity.Factory<IPrivilegedAuthorToCompositionSourceEntity>()

    val author: IAuthorEntity
}

open class PrivilegedAuthorToCompositionSourcesModel(alias: String?) :
    Table<IPrivilegedAuthorToCompositionSourceEntity>("privileged_author_to_composition_sources", alias) {
    companion object : PrivilegedAuthorToCompositionSourcesModel(null)

    override fun aliased(alias: String) = PrivilegedAuthorToCompositionSourcesModel(alias)

    val deletion = int("deletion").bindTo { it.deletion } // todo swap out for delete
    // todo - pointless because who ever is associated to record is whoever has privileges to view. Disassociated them
    //  by deleting the record from this table to remove them from viewing
    val modify = int("modify").bindTo { it.modify }
    val modifyUserPrivileges = int("modify_user_privileges").bindTo { it.modifyUserPrivileges }
//    val authorId = int("author_id").bindTo { it.authorId }
    val authorId = int("author_id").references(AuthorsModel) { it.author }
    val sourceId = int("source_id").bindTo { it.sourceId }
}
