package repositories

import configurations.IAppEnv
import org.koin.core.component.inject
import org.ktorm.database.Database

open class RepositoryBase : IRepositoryBase {
    override val appEnv: IAppEnv by inject()

    override val database: Database = Database.connect(
        appEnv.dbConnection.getProperty("jdbcUrl"),
        user = appEnv.dbConnection.getProperty("username"),
        password = appEnv.dbConnection.getProperty("password")
    )
}