package com.idealIntent.models.privileges

import models.privileges.IPrivilegeSource
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface IPrivilegeSourceEntity : Entity<IPrivilegeSourceEntity>, IPrivilegeSource {
    companion object : Entity.Factory<IPrivilegeSourceEntity>()
}

open class PrivilegeSourcesModel(alias: String?) : Table<IPrivilegeSourceEntity>("privilege_sources", alias) {
    companion object : PrivilegeSourcesModel(null)

    override fun aliased(alias: String) = PrivilegeSourcesModel(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val privilegeLevel = int("mod_lvl").bindTo { it.privilegeLevel }
}
