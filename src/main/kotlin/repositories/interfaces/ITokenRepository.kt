package repositories.interfaces

import models.authorization.Token

interface ITokenRepository {
    fun insertTokens(
        refreshToken: String, accessToken: String, authorId: Int
    ): Int

    fun deleteOldTokens(authorId: Int): Int
    fun getTokensByAuthorId(authorId: Int): Token?
}