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

    override fun validateCredentials(email: String, password: String): Author? {
        var result = database.author.find() { it.email eq email }
        return result
    }

    override fun createAuthor(request: CreateAuthorRequest): Int {
        var passwordId = database.insertAndGenerateKey(Passwords) {
            set(it.password, request.encryptedPassword)
        }

        if (passwordId !is Int)
            throw Exception("Server error. Saving password failed")

        var authorId = database.insertAndGenerateKey(Authors) {
            set(it.username, request.username)
            set(it.firstname, request.firstname)
            set(it.lastname, request.lastname)
            set(it.passwordId, passwordId)
            set(it.email, request.email)
        }

        if (authorId !is Int)
            throw Exception("Server error. Saving author failed")

        var result = database.author.find() { it.id eq authorId }

        return authorId
    }

//    override fun DeleteAuthor()

    override fun getByEmail(email: String): Author? {
        val author = database.author.find { it.email eq email }
        return author
    }

    override fun getById(authorId: Int): Author? {
        val author = database.author.find { it.id eq authorId }
        return author
    }

    override fun resetPasswordByEmail(email: String, oldPassword: String) {
        throw NotImplementedError()
    }

    override fun resetPasswordByUsername(username: String, oldPassword: String) {
        throw NotImplementedError()
    }
}