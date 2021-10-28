package repositories.interfaces

import models.authorization.Password

interface IPasswordRepository {
    fun getPassword(authorId: Int): Password?
    fun insertPassword(passwordHash: String): Int?
    fun deletePassword(authorId: Int): Int
}