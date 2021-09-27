package repositories

import models.Author

interface IAuthorViewerRepository {
    fun Get(username: String): Author?
    fun Get(authorId: Int): Author?
}