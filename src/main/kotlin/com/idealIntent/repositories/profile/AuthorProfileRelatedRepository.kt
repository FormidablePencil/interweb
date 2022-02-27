package com.idealIntent.repositories.profile

import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.repositories.RepositoryBase
import models.profile.AccountsModel
import models.profile.AuthorDetails
import models.profile.AuthorsModel
import models.profile.IAuthorEntity
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf

// todo - rename to ProfileRepository or something like that
class AuthorProfileRelatedRepository() : RepositoryBase() {
    private val Database.authors get() = this.sequenceOf(AuthorsModel)

    companion object {
        val author = AuthorsModel.aliased("author")
    }

    fun getAuthorWithDetail(authorId: Int): AuthorWithDetail =
        database.from(AuthorsModel)
            .leftJoin(AuthorDetails, AuthorsModel.id eq AuthorDetails.authorId)
            .select(
                AuthorsModel.id,
                AuthorsModel.username,
                AuthorDetails.firstname,
                AuthorDetails.lastname,
            ).where(AuthorsModel.id eq authorId).map { row ->
                AuthorWithDetail(
                    authorId = row[AuthorsModel.id],
                    username = row[AuthorsModel.username],
                    firstname = row[AuthorDetails.firstname],
                    lastname = row[AuthorDetails.lastname]
                )
            }.first()

    fun getAuthorWithDetailAndAccount(authorId: Int): AuthorWithDetailAndAccount? =
        database.from(AuthorsModel)
            .leftJoin(AuthorDetails, AuthorsModel.id eq AuthorDetails.authorId)
            .leftJoin(AccountsModel, AuthorsModel.id eq AccountsModel.authorId)
            .select(
                AuthorsModel.id,
                AuthorsModel.username,
                AuthorDetails.firstname,
                AuthorDetails.lastname,
                AccountsModel.email,
            ).where(AuthorsModel.id eq authorId).map { row ->
                if (row[AuthorsModel.id] == null) return null
                AuthorWithDetailAndAccount(
                    authorId = row[AuthorsModel.id]!!,
                    username = row[AuthorsModel.username]!!,
                    firstname = row[AuthorDetails.firstname],
                    lastname = row[AuthorDetails.lastname],
                    email = row[AccountsModel.email]
                )
            }.first()


    fun createNewAuthor(request: CreateAuthorRequest): Int? {
        val author = IAuthorEntity {
            username = request.username
        }

        if (database.authors.add(author) == 0) return null

        database.insert(AuthorDetails) {
            set(it.authorId, author.id)
            set(it.firstname, request.firstname)
            set(it.lastname, request.lastname)
        }

        database.insert(AccountsModel) {
            set(it.authorId, author.id)
            set(it.email, request.email)
        }

        return author.id
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