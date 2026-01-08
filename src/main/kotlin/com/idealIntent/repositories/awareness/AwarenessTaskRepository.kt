package com.idealIntent.repositories.awareness

import com.idealIntent.models.awareness.AwarenessTasksModel
import com.idealIntent.models.awareness.IAwarenessTaskEntity
import com.idealIntent.repositories.RepositoryBase
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class AwarenessTaskRepository : RepositoryBase() {
    private val Database.tasks get() = this.sequenceOf(AwarenessTasksModel)

    fun insert(id: String, title: String, description: String? = null, status: String = "b2d4b2"): Boolean {
        return database.insert(AwarenessTasksModel) {
            set(it.id, id)
            set(it.title, title)
            set(it.description, description)
            set(it.status, status)
        } != 0
    }

    fun getById(id: String): IAwarenessTaskEntity? {
        return database.tasks.find { it.id eq id }
    }

    fun getAll(): List<IAwarenessTaskEntity> {
        return database.from(AwarenessTasksModel).select().map { AwarenessTasksModel.createEntity(it) }
    }

    fun update(id: String, title: String? = null, description: String? = null, status: String? = null): Boolean {
        return database.update(AwarenessTasksModel) {
            title?.let { set(AwarenessTasksModel.title, it) }
            description?.let { set(AwarenessTasksModel.description, it) }
            status?.let { set(AwarenessTasksModel.status, it) }
            where { AwarenessTasksModel.id eq id }
        } != 0
    }

    fun delete(id: String): Boolean {
        return database.delete(AwarenessTasksModel) { it.id eq id } != 0
    }

    fun exists(id: String): Boolean {
        return database.tasks.find { it.id eq id } != null
    }
}