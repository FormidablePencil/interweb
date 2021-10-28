package configurations

import configurations.interfaces.IAppEnv
import io.ktor.config.*
import java.util.*

data class AppEnv(
    override val appConfig: ApplicationConfig,
    override val dbConnection: Properties
): IAppEnv