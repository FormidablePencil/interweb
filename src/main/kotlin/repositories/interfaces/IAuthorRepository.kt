package repositories.interfaces

import models.profile.Author
import serialized.CreateAuthorRequest

interface IAuthorRepository {
    fun insertAuthor(request: CreateAuthorRequest): Int?
    fun getByEmail(email: String): Author?
    fun getById(authorId: Int): Author?
    fun getIdByUsername(username: String): Author?
}