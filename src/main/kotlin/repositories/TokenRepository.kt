package repositories

import models.authorization.Token
import models.authorization.Tokens
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.interfaces.ITokenRepository

class TokenRepository : RepositoryBase(), ITokenRepository {
    private val Database.token get() = this.sequenceOf(Tokens)

    override fun insertTokens(refreshToken: String, accessToken: String, authorId: Int): Int {
        return database.insert(Tokens) {
            set(it.accessToken, accessToken)
            set(it.refreshToken, refreshToken)
            set(it.authorId, authorId)
        }
    }

    override fun deleteOldTokens(authorId: Int): Int {
        return database.delete(Tokens) { it.authorId eq authorId }
    }

    override fun getTokensByAuthorId(authorId: Int): Token? {
        return database.token.find { it.authorId eq authorId }
    }
}