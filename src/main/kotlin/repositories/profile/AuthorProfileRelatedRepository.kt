package repositories.profile

import models.profile.Accounts
import models.profile.Author
import models.profile.AuthorDetails
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase
import serialized.CreateAuthorRequest

class AuthorProfileRelatedRepository : RepositoryBase() {
    private val Database.authors get() = this.sequenceOf(Authors)

    fun createNewAuthor(request: CreateAuthorRequest): Int? {
        val author = Author {
            username = request.username
        }

        if (database.authors.add(author) == 0) return null

        database.insert(AuthorDetails) {
            set(it.authorId, author.id)
            set(it.firstname, request.firstname)
            set(it.lastname, request.lastname)
        }

        database.insert(Accounts) {
            set(it.authorId, author.id)
            set(it.email, request.email)
        }

        return author.id
    }

    fun getAuthorWithDetail(authorId: Int): AuthorWithDetail {
        return database.from(Authors)
            .leftJoin(AuthorDetails, Authors.id eq AuthorDetails.authorId)
            .select(
                Authors.id,
                Authors.username,
                AuthorDetails.firstname,
                AuthorDetails.lastname,
            ).where(Authors.id eq authorId).map { row ->
                AuthorWithDetail(
                    authorId = row[Authors.id],
                    username = row[Authors.username],
                    firstname = row[AuthorDetails.firstname],
                    lastname = row[AuthorDetails.lastname]
                )
            }.first()
    }

    fun getAuthorWithDetailAndAccount(authorId: Int): AuthorWithDetailAndAccount? {
        return database.from(Authors)
            .leftJoin(AuthorDetails, Authors.id eq AuthorDetails.authorId)
            .leftJoin(Accounts, Authors.id eq Accounts.authorId)
            .select(
                Authors.id,
                Authors.username,
                AuthorDetails.firstname,
                AuthorDetails.lastname,
                Accounts.email,
            ).where(Authors.id eq authorId).map { row ->
                if (row[Authors.id] == null) return null
                AuthorWithDetailAndAccount(
                    authorId = row[Authors.id]!!,
                    username = row[Authors.username]!!,
                    firstname = row[AuthorDetails.firstname],
                    lastname = row[AuthorDetails.lastname],
                    email = row[Accounts.email]
                )
            }.first()
    }
}

class AuthorWithDetail(val authorId: Int?, val username: String?, val firstname: String?, val lastname: String?)
class AuthorWithDetailAndAccount(
    val authorId: Int,
    val username: String,
    val firstname: String?,
    val lastname: String?,
    val email: String?
)