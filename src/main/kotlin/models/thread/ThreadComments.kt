package models.thread

import org.ktorm.schema.Table
import org.ktorm.schema.int

object ThreadComments : Table<Nothing>("threading_comments") {
    val id = int("id").primaryKey()
//    val name = varchar("name")
//    val location = varchar("location")
}