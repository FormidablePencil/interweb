package models

import org.ktorm.schema.Table
import org.ktorm.schema.int

object Group : Table<Nothing>("group") {
    val id = int("id").primaryKey()
//    val name = varchar("name")
//    val location = varchar("location")
}