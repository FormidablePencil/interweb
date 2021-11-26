package repositories

import models.authorization.Passwords
import models.profile.Author
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import serialized.CreateAuthorRequest

open class AuthorRepository : RepositoryBase() {
    val Database.author get() = this.sequenceOf(Authors)
    val Database.password get() = this.sequenceOf(Passwords)

    fun insertAuthor(request: CreateAuthorRequest): Int? {
        return database.insertAndGenerateKey(Authors) {
            set(it.username, request.username)
            set(it.firstname, request.firstname)
            set(it.lastname, request.lastname)
            set(it.email, request.email)
        } as Int?
    }

    fun getByEmail(email: String): Author? {
        return database.author.find { it.email eq email }
    }

    fun getIdByUsername(username: String): Author? {
        return database.author.find { it.username eq username }
    }

    fun getById(authorId: Int): Author? {
        return database.author.find { it.id eq authorId }
    }
}