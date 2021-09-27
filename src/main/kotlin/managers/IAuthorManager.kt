package managers

import models.Author

interface IAuthorManager {
    fun GetAuthor(username: String): Author?
    fun GetAuthor(userId: Int): Author?
}