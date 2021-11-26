package repositories

import configurations.interfaces.IConnectionToDb
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database

open class RepositoryBase : KoinComponent {
    private val connectionToDb: IConnectionToDb by inject()
    val database: Database = connectionToDb.database
}