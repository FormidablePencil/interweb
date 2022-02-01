package com.idealIntent.models.auth

import models.auth.IToken
import models.profile.AuthorsModel
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ITokenEntity : Entity<ITokenEntity>, IToken {
    companion object : Entity.Factory<ITokenEntity>()
}

object TokensModel : Table<ITokenEntity>("tokens") {
    val id = int("id").primaryKey().bindTo { it.id }
    val refreshToken = varchar("refresh_token").bindTo { it.refreshToken }
    val authorId = int("author_id").references(AuthorsModel) { it.author }
}