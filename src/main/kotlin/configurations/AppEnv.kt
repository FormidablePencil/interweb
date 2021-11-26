package configurations

import io.ktor.config.*
import java.util.*

data class AppEnv(
    val appConfig: ApplicationConfig, // todo - should deprecate public in place of private
    val dbConnection: Properties,
) {
    fun getConfig(path: String): String {
        return appConfig.property(path).getString()
    }
}