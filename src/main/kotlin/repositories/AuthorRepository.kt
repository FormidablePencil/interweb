package repositories

import dtos.author.CreateAuthorRequest
import models.authorization.Passwords
import models.profile.Author
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.interfaces.IAuthorRepository

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
        return database.author.find { it.email eq email }
    }

    override fun getByUsername(username: String): Author? {
        return database.author.find { it.username eq username }
    }

    override fun getById(authorId: Int): Author? {
        return database.author.find { it.id eq authorId }
    }
}