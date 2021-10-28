package configurations

import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database

class ConnectionToDb : KoinComponent, IConnectionToDb {
    override val appEnv: IAppEnv by inject()

    override val database: Database = Database.connect(
        appEnv.dbConnection.getProperty("jdbcUrl"),
        user = appEnv.dbConnection.getProperty("username"),
        password = appEnv.dbConnection.getProperty("password")
    )
}
