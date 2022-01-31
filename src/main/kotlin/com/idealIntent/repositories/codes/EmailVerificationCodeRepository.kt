package com.idealIntent.repositories.codes

import com.idealIntent.repositories.RepositoryBase
import models.codes.EmailVerificationCodesModel
import models.codes.IEmailVerificationCodeEntity
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

// todo - comments
/**
 * Email verification code repository
 *
 * @constructor Create empty Email verification code repository
 */
class EmailVerificationCodeRepository : RepositoryBase() {
    private val Database.emailVerifyCodes get() = this.sequenceOf(EmailVerificationCodesModel)

    fun insert(code: String, authorId: Int): Boolean {
        return database.insert(EmailVerificationCodesModel) {
            set(it.code, code)
            set(it.authorId, authorId)
        } == 1
    }

    fun get(authorId: Int): IEmailVerificationCodeEntity? {
        return database.emailVerifyCodes.find {
            it.authorId eq authorId
        }
    }

    fun getCode(authorId: Int): String? {
        return get(authorId)?.code
    }

    fun delete(authorId: Int): Boolean {
        return database.delete(EmailVerificationCodesModel){
            it.authorId eq authorId
        } != 0
    }
}