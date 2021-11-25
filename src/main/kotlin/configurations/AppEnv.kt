package configurations

import configurations.interfaces.IAppEnv
import io.ktor.config.*
import java.util.*

data class AppEnv(
    override val appConfig: ApplicationConfig, // todo - should deprecate public in place of private
    override val dbConnection: Properties,
) : IAppEnv {
    override fun getConfig(path: String): String {
        return appConfig.property(path).getString()
    }
}