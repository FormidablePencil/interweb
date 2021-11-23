package repositories

import models.authorization.Passwords
import models.profile.Author
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
// todo - replace findLast with find and run the tests
import org.ktorm.entity.find
import org.ktorm.entity.findLast
import org.ktorm.entity.sequenceOf
import repositories.interfaces.IAuthorRepository
import serialized.CreateAuthorRequest

open class AuthorRepository : RepositoryBase(), IAuthorRepository {
    val Database.author get() = this.sequenceOf(Authors)
    val Database.password get() = this.sequenceOf(Passwords)

    override fun insertAuthor(request: CreateAuthorRequest): Int? {
        return database.insertAndGenerateKey(Authors) {
            set(it.username, request.username)
            set(it.firstname, request.firstname)
            set(it.lastname, request.lastname)
            set(it.email, request.email)
        } as Int?
    }

    override fun getByEmail(email: String): Author? {
        return database.author.findLast { it.email eq email }
    }

    override fun getByUsername(username: String): Author? {
        return database.author.findLast { it.username eq username }
    }

    override fun getById(authorId: Int): Author? {
        return database.author.findLast { it.id eq authorId }
    }
}