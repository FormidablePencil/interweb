package configurations

import io.ktor.config.*
import java.util.*

interface IConfig {
    val appConfig: ApplicationConfig
    val dbConnection: Properties
}

class Config(
    override val appConfig: ApplicationConfig,
    override val dbConnection: Properties
) : IConfig