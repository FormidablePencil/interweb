package repositories

import dto.token.AuthenticateResponse

class TokenRepository: RepositoryBase() {
//    val Database.author get() = this.sequenceOf(Authors)

    fun Authenticate(request: AuthenticateResponse) {
       // check to see if there's a token in db created for that user and matches
//        database.author
    }
}