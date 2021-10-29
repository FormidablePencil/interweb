package repositories.interfaces

import models.authorization.Token

interface IRefreshTokenRepository {
    fun insertToken(refreshToken: String, authorId: Int): Int
    fun deleteOldToken(authorId: Int): Int
    fun getTokenByAuthorId(authorId: Int): Token?
}