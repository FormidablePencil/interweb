package repositories

import models.Authors
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

open class RepositoryBase {
    val database: Database

    init {
        val prop = Properties()
        prop.load(FileInputStream("local.datasource.properties"))
        database = Database.connect(prop.getProperty("jdbcUrl"), user = prop.getProperty("username"), password = prop.getProperty("password"))
        println("${prop.getProperty("password")} ${prop.getProperty("username")} ${prop.getProperty("jdbcUrl")}")
    }

}