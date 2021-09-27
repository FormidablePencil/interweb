package models

import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.isNotNull
import org.ktorm.entity.Entity
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface Author : Entity<Author> {
    companion object : Entity.Factory<Author>()
    val id: Int
    var username: String
}

object Authors : Table<Author>("t_config") {
    val id = int("id").primaryKey()
    val username = varchar("username")
}

//val Database.staffs get() = this.sequenceOf(Staffs)