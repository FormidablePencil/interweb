package models.token

import models.Author
import models.Authors
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface Token : Entity<Token> {
    companion object : Entity.Factory<Token>()

    val id: Int
    val refreshToken: String
    val accessToken: String
    val author: Author
}

object Tokens : Table<Token>("token") {
    val id = int("id").primaryKey().bindTo { it.id }
    val refreshToken = varchar("refresh_token").bindTo { it.refreshToken }
    val accessToken = varchar("access_token").bindTo { it.accessToken }
    val authorId = int("author_id").references(Authors) { it.author }
}