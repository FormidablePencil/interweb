package repositories.profile

import models.profile.Author
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

open class AuthorRepository : RepositoryBase() {
    private val Database.authors get() = this.sequenceOf(Authors)

    // insert -> AuthorProfileRelatedRepository

    fun getByUsername(username: String): Author? {
        return database.authors.find { it.username eq username }
    }

    fun getById(authorId: Int): Author? {
        return database.authors.find { it.id eq authorId }
    }
}