package managers.interfaces

import models.profile.Author

interface IAuthorManager {
    fun GetAuthorByEmail(username: String): Author?
    fun GetAuthorById(userId: Int): Author?
}