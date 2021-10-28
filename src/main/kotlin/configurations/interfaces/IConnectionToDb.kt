package configurations.interfaces

import org.ktorm.database.Database

interface IConnectionToDb {
    val appEnv: IAppEnv
    val database: Database
}
