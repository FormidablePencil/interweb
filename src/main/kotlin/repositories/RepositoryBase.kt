package repositories

import configurations.IConnectionToDb
import org.koin.core.component.inject
import org.ktorm.database.Database

open class RepositoryBase : IRepositoryBase {
    private val connectionToDb by inject<IConnectionToDb>()
    override val database: Database = connectionToDb.database
}