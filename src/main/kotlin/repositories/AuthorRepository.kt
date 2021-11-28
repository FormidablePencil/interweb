package repositories

import models.profile.Author
import models.profile.AuthorDetails
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import serialized.CreateAuthorRequest

open class AuthorRepository : RepositoryBase() {
    private val Database.authors get() = this.sequenceOf(Authors)

    fun insert(request: CreateAuthorRequest): Int? {
        return database.insertAndGenerateKey(Authors) {
            set(it.username, request.username)
            // todo - stuff moved to different tables
//            set(it.firstname, request.firstname)
//            set(it.lastname, request.lastname)
//            set(it.email, request.email)
        } as Int?
    }

    fun getByEmail(email: String): Author? {
        // todo - stuff moved to different tables
//        return database.authors.find { it.email eq email }
        return null
    }

    fun getByUsername(username: String): Author? {
        return database.authors.find { it.username eq username }
    }

    fun getById(authorId: Int): Author? {
        return database.authors.find { it.id eq authorId }
    }

    fun getAuthorAndDetails(authorId: Int): CombinedAuthor {
        return database.from(Authors)
            .leftJoin(AuthorDetails, Authors.id eq AuthorDetails.authorId)
            .select(
                Authors.id,
                Authors.username,
                AuthorDetails.firstname,
                AuthorDetails.lastname,
            ).where(Authors.id eq authorId).map { row ->
                CombinedAuthor(
                    authorId = row[Authors.id],
                    username = row[Authors.username],
                    firstname = row[AuthorDetails.firstname],
                    lastname = row[AuthorDetails.lastname]
                )
            }.first()
    }
}

class CombinedAuthor(val authorId: Int?, val username: String?, val firstname: String?, val lastname: String?)