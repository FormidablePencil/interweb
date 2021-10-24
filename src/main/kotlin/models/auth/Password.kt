package models.auth

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface Password : Entity<Password> {
    companion object : Entity.Factory<Password>()
    var id: Int
    var password: String
    var created: LocalDateTime
}

object Passwords : Table<Password>("passwords") {
    val id = int("id").primaryKey().bindTo { it.id }
    val password = varchar("password").bindTo { it.password }
    val created = datetime("created").bindTo { it.created }
}
