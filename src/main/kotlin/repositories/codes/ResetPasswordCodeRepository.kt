package repositories.codes

import models.codes.ResetPasswordCode
import models.codes.ResetPasswordCodes
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class ResetPasswordCodeRepository : RepositoryBase() {
    private val Database.resetPasswordCode get() = this.sequenceOf(ResetPasswordCodes)

    fun insert(code: String, authorId: Int): Boolean {
        return database.insert(ResetPasswordCodes) {
            set(it.code, code)
            set(it.authorId, authorId)
        } == 1
    }

    fun get(authorId: Int): ResetPasswordCode? {
        return database.resetPasswordCode.find {
            it.authorId eq authorId
        }
    }

    fun getCode(authorId: Int): String? {
        return get(authorId)?.code
    }

    fun delete(authorId: Int) {
        database.delete(ResetPasswordCodes) {
            it.authorId eq authorId
        }
    }
}