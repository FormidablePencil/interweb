package models

import org.ktorm.schema.Table
import org.ktorm.schema.int

object GroupComments : Table<Nothing>("grouping_comments") {
    val id = int("id").primaryKey()
//    val name = varchar("name")
//    val location = varchar("location")
}