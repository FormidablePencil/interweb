package repositories.interfaces

import dtos.author.CreateAuthorRequest
import models.profile.Author

interface IAuthorRepository {
    fun createAuthor(request: CreateAuthorRequest): Int?
    fun getByEmail(email: String): Author?
    fun getById(authorId: Int): Author?
    fun resetPasswordByEmail(email: String, oldPassword: String)
    fun resetPasswordByUsername(username: String, oldPassword: String)
    fun getByUsername(username: String): Author?
}