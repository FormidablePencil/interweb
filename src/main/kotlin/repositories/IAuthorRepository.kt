package repositories

import models.Author

interface IAuthorRepository {
    fun CreateAuthor(username: String): Int
    fun Get(username: String): Author?
    fun Get(authorId: Int): Author?
}