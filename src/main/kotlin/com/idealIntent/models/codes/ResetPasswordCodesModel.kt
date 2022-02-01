package com.idealIntent.models.codes

import models.codes.IResetPasswordCode
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface IResetPasswordCodeEntity : Entity<IResetPasswordCodeEntity>, IResetPasswordCode {
    companion object : Entity.Factory<IResetPasswordCodeEntity>()

    val authorId: Int
    val code: String
    val created: LocalDateTime
}

object ResetPasswordCodesModel : Table<IResetPasswordCodeEntity>("reset_password_codes") {
    val authorId = int("author_id").primaryKey().bindTo { it.authorId }
    val code = varchar("code").bindTo { it.code }
    val created = datetime("created").bindTo { it.created }
}
