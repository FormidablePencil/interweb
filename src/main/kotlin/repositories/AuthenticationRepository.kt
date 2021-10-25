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
}