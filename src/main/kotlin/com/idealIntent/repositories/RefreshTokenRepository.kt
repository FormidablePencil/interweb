package com.idealIntent.repositories

import models.authorization.ITokenEntity
import models.authorization.TokensModel
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class RefreshTokenRepository : RepositoryBase() {
    private val Database.token get() = this.sequenceOf(TokensModel)

    fun insert(refreshToken: String, authorId: Int): Boolean {
        return database.insert(TokensModel) {
            set(it.refreshToken, refreshToken)
            set(it.authorId, authorId)
        } != 0
    }

    fun delete(authorId: Int): Boolean {
        return database.delete(TokensModel) { it.authorId eq authorId } != 0
    }

    fun get(authorId: Int): ITokenEntity? {
        return database.token.find { it.authorId eq authorId }
    }
}