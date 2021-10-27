package configurations

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database

open class ConnectionToDb : KoinComponent, IConnectionToDb {
    override val appEnv: IAppEnv by inject()

    override val database: Database = Database.connect(
        appEnv.dbConnection.getProperty("jdbcUrl"),
        user = appEnv.dbConnection.getProperty("username"),
        password = appEnv.dbConnection.getProperty("password")
    )
}
