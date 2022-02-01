package com.idealIntent.models.codes

import models.codes.IEmailVerificationCode
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface IEmailVerificationCodeEntity : Entity<IEmailVerificationCodeEntity>, IEmailVerificationCode {
    companion object : Entity.Factory<IEmailVerificationCodeEntity>()

    val authorId: Int
    val code: String
    val created: LocalDateTime
}

object EmailVerificationCodesModel : Table<IEmailVerificationCodeEntity>("email_verification_codes") {
    val authorId = int("author_id").primaryKey().bindTo { it.authorId }
    val code = varchar("code").bindTo { it.code }
    val created = datetime("created").bindTo { it.created }
}
