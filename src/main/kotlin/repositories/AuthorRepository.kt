package repositories

import models.Author
import models.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNotNull
import org.ktorm.entity.*

open class AuthorRepository : RepositoryBase(), IAuthorRepository {
    val Database.author get() = this.sequenceOf(Authors)

    override fun CreateAuthor(username: String): Int {
        val author = Author{ username }
        val response = database.author.add(author)
        return response
    }

    override fun GetByUsername(username: String): Author? {
        val author = database.author.find { it.username eq username }
        return author
    }

    override fun GetById(authorId: Int): Author? {
        val author = database.author.find { it.id eq authorId }
        return author
    }
}