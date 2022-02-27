package com.idealIntent.models.privileges

import models.compositionLayout.ICompositionSourceToLayout
import models.privileges.IPrivilegeSource
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

data class CompositionSourceToLayout(
    override val sourceId: Int,
    override val compositionCategory: Int,
    override val compositionType: Int,
    override val orderRank: Int
): ICompositionSourceToLayout



interface ICompositionSourceEntity : Entity<ICompositionSourceEntity>, IPrivilegeSource {
    companion object : Entity.Factory<ICompositionSourceEntity>()

    val name: String
}

open class CompositionSourcesModel(alias: String?) : Table<ICompositionSourceEntity>("composition_sources", alias) {
    companion object : CompositionSourcesModel(null)

    override fun aliased(alias: String) = CompositionSourcesModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val privilegeLevel = int("privilege_level").bindTo { it.privilegeLevel }
    val compositionType = int("composition_type").bindTo { it.compositionType }
}
