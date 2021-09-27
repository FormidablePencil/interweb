package repositories

import models.Author
import org.ktorm.dsl.eq
import org.ktorm.entity.find

class AuthorViewerRepository : AuthorRepository(), IAuthorViewerRepository {
    override fun Get(username: String): Author? {
        val author = database.author.find { it.username eq username }
        return author
    }

    override fun Get(authorId: Int): Author? {
        val author = database.author.find { it.id eq authorId }
        return author
    }
}