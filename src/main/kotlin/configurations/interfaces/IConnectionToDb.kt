package configurations.interfaces

import configurations.AppEnv
import org.ktorm.database.Database

interface IConnectionToDb {
    val appEnv: AppEnv
    val database: Database
}
