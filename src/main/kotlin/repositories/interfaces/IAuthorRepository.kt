package repositories.interfaces

import dtos.author.CreateAuthorRequest
import models.profile.Author

interface IAuthorRepository {
    fun insertAuthor(request: CreateAuthorRequest): Int?
    fun getByEmail(email: String): Author?
    fun getById(authorId: Int): Author?
    fun getByUsername(username: String): Author?
}