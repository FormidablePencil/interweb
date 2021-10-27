package configurations

import org.ktorm.database.Database

interface IConnectionToDb {
    val appEnv: IAppEnv
    val database: Database
}
