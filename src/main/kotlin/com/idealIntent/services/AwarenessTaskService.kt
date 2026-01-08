package com.idealIntent.services

import com.idealIntent.repositories.awareness.AwarenessTaskRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AwarenessTaskService : KoinComponent {
    private val taskRepository: AwarenessTaskRepository by inject()

    // Basic CRUD operations for task context saving
    fun saveTaskContext(id: String, title: String, description: String? = null, status: String = "b2d4b2"): Boolean {
        return taskRepository.insert(id, title, description, status)
    }

    fun getTaskContext(id: String) = taskRepository.getById(id)

    fun getAllTaskContexts() = taskRepository.getAll()

    fun updateTaskContext(id: String, title: String? = null, description: String? = null, status: String? = null): Boolean {
        return taskRepository.update(id, title, description, status)
    }

    fun deleteTaskContext(id: String): Boolean {
        return taskRepository.delete(id)
    }

    fun taskExists(id: String): Boolean {
        return taskRepository.exists(id)
    }
}