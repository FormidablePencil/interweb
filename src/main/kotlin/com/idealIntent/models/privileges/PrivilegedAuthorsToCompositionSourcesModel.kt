package com.idealIntent.models.privileges

import models.privileges.IPrivilegedAuthorsToComposition
import models.profile.AuthorsModel
import models.profile.IAuthorEntity
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IPrivilegedAuthorsToCompositionSourceEntity :
    Entity<IPrivilegedAuthorsToCompositionSourceEntity>, IPrivilegedAuthorsToComposition {
    companion object : Entity.Factory<IPrivilegedAuthorsToCompositionSourceEntity>()

    val author: IAuthorEntity
}

open class PrivilegedAuthorsToCompositionSourcesModel(alias: String?) :
    Table<IPrivilegedAuthorsToCompositionSourceEntity>("privileged_authors_to_composition_sources", alias) {
    companion object : PrivilegedAuthorsToCompositionSourcesModel(null)

    override fun aliased(alias: String) = PrivilegedAuthorsToCompositionSourcesModel(alias)

    val view = int("view").bindTo { it.view } // todo swap out for delete
    // todo - pointless because who ever is associated to record is whoever has privileges to view. Disassociated them
    //  by deleting the record from this table to remove them from viewing
    val modify = int("modify").bindTo { it.modify }
//    val authorId = int("author_id").bindTo { it.authorId }
    val authorId = int("author_id").references(AuthorsModel) { it.author }
    val sourceId = int("source_id").bindTo { it.sourceId }
}
