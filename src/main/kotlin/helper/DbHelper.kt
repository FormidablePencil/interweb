package helper

import configurations.IConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database

open class DbHelper : KoinComponent {
    private val config by inject<IConfig>()
    val database: Database = Database.connect(
        config.dbConnection.getProperty("jdbcUrl"),
        user = config.dbConnection.getProperty("username"),
        password = config.dbConnection.getProperty("password")
    )
}
