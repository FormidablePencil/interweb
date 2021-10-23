package managers

import models.Author

interface IAuthorManager {
    fun GetAuthorByUsername(username: String): Author?
    fun GetAuthorById(userId: Int): Author?
}