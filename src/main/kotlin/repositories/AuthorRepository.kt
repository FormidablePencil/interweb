package repositories

import dto.author.CreateAuthorRequest
import models.Author
import models.Authors
import models.auth.Passwords
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

open class AuthorRepository : RepositoryBase(), IAuthorRepository {
    val Database.author get() = this.sequenceOf(Authors)
    val Database.password get() = this.sequenceOf(Passwords)

    override fun createAuthor(request: CreateAuthorRequest): Int {

        var authorId = database.insertAndGenerateKey(Authors) {
            set(it.username, request.username)
            set(it.firstname, request.firstname)
            set(it.lastname, request.lastname)
            set(it.email, request.email)
        }

        if (authorId !is Int)
            throw Exception("Server error. Saving author failed")

        var result = database.author.find() { it.id eq authorId }

        return authorId
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

    override fun resetPasswordByEmail(email: String, oldPassword: String) {
        throw NotImplementedError()
    }

    override fun resetPasswordByUsername(username: String, oldPassword: String) {
        throw NotImplementedError()
    }
}