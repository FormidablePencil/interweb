package com.idealIntent.repositories.profile

import com.idealIntent.repositories.RepositoryBase
import models.profile.AuthorsModel
import models.profile.IAuthorEntity
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

open class AuthorRepository : RepositoryBase() {
    private val Database.authors get() = this.sequenceOf(AuthorsModel)

    // insert -> AuthorProfileRelatedRepository

    fun getByUsername(username: String): IAuthorEntity? {
        return database.authors.find { it.username eq username }
    }

    fun getById(authorId: Int): IAuthorEntity? {
        return database.authors.find { it.id eq authorId }
    }
}