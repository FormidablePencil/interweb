package repositories

import dto.Token.AuthenticateRequest
import models.Authors
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

class TokenRepository: RepositoryBase() {
    val Database.author get() = this.sequenceOf(Authors)

    fun Authenticate(request: AuthenticateRequest) {
       // check to see if there's a token in db created for that user and matches
        database.author
    }
}