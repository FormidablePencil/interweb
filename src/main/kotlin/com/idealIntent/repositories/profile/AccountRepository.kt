package com.idealIntent.repositories.profile

import models.profile.Account
import models.profile.Accounts
import models.profile.Accounts.email
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import com.idealIntent.repositories.RepositoryBase

class AccountRepository : RepositoryBase() {
    private val Database.accounts get() = this.sequenceOf(Accounts)

    // insert -> AuthorProfileRelatedRepository

    fun getById(authorId: Int): Account? {
        return database.accounts.find { it.email eq email }
    }

    fun getByEmail(email: String): Account? {
        return database.accounts.find { it.email eq email }
    }
}