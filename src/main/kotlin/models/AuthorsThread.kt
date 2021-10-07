package models

import org.ktorm.schema.Table
import org.ktorm.schema.int

object AuthorsThreads : Table<Nothing>("author") {
    val id = int("id").primaryKey()
    val authorId = int("id")
//    val name = varchar("name")
//    val location = varchar("location")
}