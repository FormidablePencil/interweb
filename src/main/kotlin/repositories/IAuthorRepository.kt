package repositories

import dto.author.CreateAuthorRequest
import models.Author

interface IAuthorRepository {
    fun validateCredentials(email: String, password: String): Author?
    fun createAuthor(request: CreateAuthorRequest): Int
    fun getByEmail(email: String): Author?
    fun getById(authorId: Int): Author?
    fun resetPasswordByEmail(email: String, oldPassword: String)
    fun resetPasswordByUsername(username: String, oldPassword: String)
}