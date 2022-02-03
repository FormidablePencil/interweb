package com.idealIntent.repositories.profile

import com.idealIntent.repositories.RepositoryBase
import models.profile.AccountsModel
import models.profile.IAccount
import models.profile.IAccountEntity
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

// todo - move AccountRepository and AuthorRepository to profile
class AccountRepository : RepositoryBase() {
    private val Database.accounts get() = this.sequenceOf(AccountsModel)

    // insert -> AuthorProfileRelatedRepository

    fun getById(authorId: Int): IAccountEntity? {
        TODO()
//        return database.accounts.find { it.email eq email }
    }

    fun getByEmail(email: String): IAccountEntity? {
        return database.accounts.find { it.email eq email }
    }
}