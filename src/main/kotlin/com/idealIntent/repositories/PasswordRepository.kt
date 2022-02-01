package com.idealIntent.repositories

import com.idealIntent.models.auth.IPasswordEntity
import com.idealIntent.models.auth.PasswordsModel
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class PasswordRepository : RepositoryBase() {
    private val Database.password get() = this.sequenceOf(PasswordsModel)

    fun insert(passwordHash: String, authorId: Int): Boolean {
        return database.insert(PasswordsModel) {
            set(it.passwordHash, passwordHash)
            set(it.authorId, authorId)
        } != 0
    }

    fun get(authorId: Int): IPasswordEntity? {
        return database.password.find { it.authorId eq authorId }
    }

    fun delete(authorId: Int): Boolean {
        return database.delete(PasswordsModel) { it.authorId eq authorId } != 0
    }
}