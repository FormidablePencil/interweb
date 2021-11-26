package configurations

import io.ktor.config.*
import org.ktorm.database.Database
import java.util.*

data class AppEnv(
    private val appConfig: ApplicationConfig, // todo - should deprecate public in place of private
    private val dbConnection: Properties,
) {
    val database: Database = Database.connect(
        dbConnection.getProperty("jdbcUrl"),
        user = dbConnection.getProperty("username"),
        password = dbConnection.getProperty("password"),
    )

    fun getConfig(path: String): String {
        return appConfig.property(path).getString()
    }
}