package com.idealIntent.models.auth

import models.auth.IPassword
import models.profile.AuthorsModel
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface IPasswordEntity : Entity<IPasswordEntity>, IPassword {
    companion object : Entity.Factory<IPasswordEntity>()
}

object PasswordsModel : Table<IPasswordEntity>("passwords") {
    val passwordHash = varchar("password").bindTo { it.passwordHash }
    val authorId = int("author_id").references(AuthorsModel) { it.author }
}
