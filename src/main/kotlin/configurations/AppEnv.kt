package configurations

import io.ktor.config.*
import java.util.*

data class AppEnv(
    override val appConfig: ApplicationConfig,
    override val dbConnection: Properties
): IAppEnv