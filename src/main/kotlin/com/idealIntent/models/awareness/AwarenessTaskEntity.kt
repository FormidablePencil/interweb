package com.idealIntent.models.awareness

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar
import java.time.LocalDateTime

import com.idealIntent.models.awareness.AwarenessStatusesModel

interface IAwarenessTaskEntity : Entity<IAwarenessTaskEntity> {
    companion object : Entity.Factory<IAwarenessTaskEntity>()
    val id: String
    val title: String
    val description: String?
    val status: IAwarenessStatusEntity
    val parentId: String?
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}

object AwarenessTasksModel : Table<IAwarenessTaskEntity>("awareness_tasks") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val title = varchar("title").bindTo { it.title }
    val description = varchar("description").bindTo { it.description }
    val status = varchar("status").references(AwarenessStatusesModel) { it.status }
    val parentId = varchar("parent_id").bindTo { it.parentId }
    val createdAt = datetime("created_at").bindTo { it.createdAt }
    val updatedAt = datetime("updated_at").bindTo { it.updatedAt }
}
