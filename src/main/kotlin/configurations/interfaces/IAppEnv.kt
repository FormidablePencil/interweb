package configurations.interfaces

import io.ktor.config.*
import java.util.*

interface IAppEnv {
    val appConfig: ApplicationConfig
    val dbConnection: Properties
}