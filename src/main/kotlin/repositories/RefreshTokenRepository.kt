package repositories

import models.authorization.Token
import models.authorization.Tokens
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class RefreshTokenRepository : RepositoryBase() {
    private val Database.token get() = this.sequenceOf(Tokens)

    fun insert(refreshToken: String, authorId: Int): Boolean {
        return database.insert(Tokens) {
            set(it.refreshToken, refreshToken)
            set(it.authorId, authorId)
        } != 0
    }

    fun delete(authorId: Int): Boolean {
        return database.delete(Tokens) { it.authorId eq authorId } != 0
    }

    fun get(authorId: Int): Token? {
        return database.token.find { it.authorId eq authorId }
    }
}