package repositories.interfaces

import models.authorization.Token

interface IRefreshTokenRepository {
    fun insertToken(refreshToken: String, authorId: Int): Boolean
    fun deleteOldToken(authorId: Int): Boolean
    fun getTokenByAuthorId(authorId: Int): Token?
}