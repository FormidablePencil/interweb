package repositories

import models.Authors
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

class TokenRepository : RepositoryBase(), ITokenRepository {
    private val Database.author get() = this.sequenceOf(Authors)

    override fun insertTokens(
        refreshToken: HashMap<String, String>,
        accessToken: HashMap<String, String>,
        authorId: Int
    ): Int {
        throw NotImplementedError()
    }

    override fun deleteOldTokens(username: String, authorId: Int) {
        // Not too familiar with tokens so we should first create them before we delete
        TODO()
    }
}