package repositories

import configurations.interfaces.IConnectionToDb
import org.koin.core.component.inject
import org.ktorm.database.Database
import repositories.interfaces.IRepositoryBase

open class RepositoryBase : IRepositoryBase {
    private val connectionToDb by inject<IConnectionToDb>()
    override val database: Database = connectionToDb.database
}