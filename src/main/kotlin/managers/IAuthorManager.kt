package managers

import models.Author

interface IAuthorManager {
    fun GetAuthorByEmail(username: String): Author?
    fun GetAuthorById(userId: Int): Author?
}