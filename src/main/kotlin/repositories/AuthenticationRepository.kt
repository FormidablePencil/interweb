package repositories

import io.ktor.application.*
import io.ktor.http.*
import models.Authors
import org.junit.Test
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

class TokenRepository : RepositoryBase(), ITokenRepository {
    private val Database.author get() = this.sequenceOf(Authors)

    override fun insertTokens(
        refreshToken: HashMap<String, String>,
        accessToken: HashMap<String, String>
    ): Int {
        return 0
    }
}