package repositories

import models.authorization.Token
import models.authorization.Tokens
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.interfaces.IRefreshTokenRepository

class RefreshTokenRepository : RepositoryBase(), IRefreshTokenRepository {
    private val Database.token get() = this.sequenceOf(Tokens)

    override fun insertToken(refreshToken: String, authorId: Int): Boolean {
        return database.insert(Tokens) {
            set(it.refreshToken, refreshToken)
            set(it.authorId, authorId)
        } != 0
    }

    override fun deleteOldToken(authorId: Int): Boolean {
        return database.delete(Tokens) { it.authorId eq authorId } != 0
    }

    override fun getTokenByAuthorId(authorId: Int): Token? {
        return database.token.find { it.authorId eq authorId }
    }
}