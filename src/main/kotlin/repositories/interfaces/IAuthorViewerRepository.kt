package repositories.interfaces

import models.profile.Author

interface IAuthorViewerRepository {
    fun Get(username: String): Author?
    fun Get(authorId: Int): Author?
}