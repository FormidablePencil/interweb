package com.idealIntent.models.awareness

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface IAwarenessStatusEntity : Entity<IAwarenessStatusEntity> {
    companion object : Entity.Factory<IAwarenessStatusEntity>()
    val id: String
    val name: String
    val description: String?
}

object AwarenessStatusesModel : Table<IAwarenessStatusEntity>("awareness_statuses") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val description = varchar("description").bindTo { it.description }
}