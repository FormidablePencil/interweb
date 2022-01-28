package com.idealIntent.repositories

import models.authorization.Password
import models.authorization.Passwords
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class PasswordRepository : RepositoryBase() {
    private val Database.password get() = this.sequenceOf(Passwords)

    fun insert(passwordHash: String, authorId: Int): Boolean {
        return database.insert(Passwords) {
            set(it.password, passwordHash)
            set(it.authorId, authorId)
        } != 0
    }

    fun get(authorId: Int): Password? {
        return database.password.find { it.authorId eq authorId }
    }

    fun delete(authorId: Int): Boolean {
        return database.delete(Passwords) { it.authorId eq authorId } != 0
    }
}