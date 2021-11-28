package repositories.codes

import models.codes.EmailVerificationCode
import models.codes.EmailVerificationCodes
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class EmailVerificationCodeRepository : RepositoryBase() {
    private val Database.emailVerifyCodes get() = this.sequenceOf(EmailVerificationCodes)

    fun insert(code: String, authorId: Int): Boolean {
        return database.insert(EmailVerificationCodes) {
            set(it.code, code)
            set(it.authorId, authorId)
        } == 1
    }

    fun get(authorId: Int): EmailVerificationCode? {
        return database.emailVerifyCodes.find {
            it.authorId eq authorId
        }
    }

    fun getCode(authorId: Int): String? {
        return get(authorId)?.code
    }

    fun delete(authorId: Int): Boolean {
        return database.delete(EmailVerificationCodes){
            it.authorId eq authorId
        } != 0
    }
}