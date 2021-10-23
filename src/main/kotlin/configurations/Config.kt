package configurations

import io.ktor.config.*

interface IConfig {
    val appConfig: ApplicationConfig
}

class Config(override val appConfig: ApplicationConfig) : IConfig