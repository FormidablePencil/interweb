package repositories.interfaces

import models.authorization.Password

interface IPasswordRepository {
    fun insertPassword(passwordHash: String, authorId: Int): Boolean
    fun getPassword(authorId: Int): Password?
    fun deletePassword(authorId: Int): Boolean
}