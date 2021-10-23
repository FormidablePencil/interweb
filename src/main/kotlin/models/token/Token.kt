package models.token

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface Token : Entity<Token> {
    companion object : Entity.Factory<Token>()
    val id: Int
    var token: String
}

object Tokens : Table<Token>("t_config") {
    val id = int("id").primaryKey()
    val token = varchar("token")
}

