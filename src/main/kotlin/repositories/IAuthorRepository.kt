package repositories

import models.Author

interface IAuthorRepository {
    fun CreateAuthor(username: String): Int
    fun GetByUsername(username: String): Author?
    fun GetById(authorId: Int): Author?
}