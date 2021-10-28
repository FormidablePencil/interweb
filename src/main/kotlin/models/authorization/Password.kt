package models.authorization

import models.profile.Author
import models.profile.Authors
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface Password : Entity<Password> {
    companion object : Entity.Factory<Password>()

    val id: Int
    val password: String
    val created: LocalDateTime
    val author: Author
}

object Passwords : Table<Password>("passwords") {
    val id = int("id").primaryKey().bindTo { it.id }
    val password = varchar("password").bindTo { it.password }
    val created = datetime("created").bindTo { it.created }
    val authorId = int("author_id").references(Authors) { it.author }
}
