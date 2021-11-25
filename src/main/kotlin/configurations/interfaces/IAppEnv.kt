package configurations.interfaces

import io.ktor.config.*
import java.util.*

interface IAppEnv {
    val appConfig: ApplicationConfig
    fun getConfig(path: String): String
    val dbConnection: Properties
}