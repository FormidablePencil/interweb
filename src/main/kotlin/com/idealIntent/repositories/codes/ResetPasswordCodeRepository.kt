package com.idealIntent.repositories.codes

import com.idealIntent.repositories.RepositoryBase
import models.codes.IResetPasswordCodeEntity
import models.codes.ResetPasswordCodesModel
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

// todo - comments
class ResetPasswordCodeRepository : RepositoryBase() {
    private val Database.resetPasswordEmailCode get() = this.sequenceOf(ResetPasswordCodesModel)

    fun insert(code: String, authorId: Int): Boolean {
        return database.insert(ResetPasswordCodesModel) {
            set(it.code, code)
            set(it.authorId, authorId)
        } == 1
    }

    fun get(authorId: Int): IResetPasswordCodeEntity? {
        return database.resetPasswordEmailCode.find {
            it.authorId eq authorId
        }
    }

    fun getCode(authorId: Int): String? {
        return get(authorId)?.code
    }

    fun delete(authorId: Int) {
        database.delete(ResetPasswordCodesModel) {
            it.authorId eq authorId
        }
    }
}