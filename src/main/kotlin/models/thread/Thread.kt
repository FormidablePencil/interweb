package models.thread

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

interface Thread : Entity<Thread> {
    companion object : Entity.Factory<Thread>()
    val id: Int
}

object Threads : Table<Nothing>("thread") {
    val id = int("id").primaryKey()
//    val name = varchar("name")
//    val location = varchar("location")
}