package models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.time.LocalDateTime


interface Author : Entity<Author> {
    companion object : Entity.Factory<Author>()
    val id: Int
    val email: String
    val username: String
    val firstname: String
    val lastname: String
    val created: LocalDateTime
    val passwordId: Int
}

object Authors : Table<Author>("authors") {
    val id = int("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val username = varchar("username").bindTo { it.username }
    val firstname = varchar("firstname").bindTo { it.firstname }
    val lastname = varchar("lastname").bindTo { it.lastname }
    val created = datetime("created").bindTo { it.created }

    // even through password_id is a foreign key we'll not join the password
    // val passwordId = int("password_id").references(Passwords) { it.password }
    val passwordId = int("password_id").bindTo { it.passwordId }
}