package repositories

import models.profile.Author
import org.ktorm.dsl.eq
import org.ktorm.entity.find

class AuthorViewerRepository : AuthorRepository() {
    fun Get(username: String): Author? {
        val author = database.author.find { it.username eq username }
        return author
    }

    fun Get(authorId: Int): Author? {
        val author = database.author.find { it.id eq authorId }
        return author
    }
}