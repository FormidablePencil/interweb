package repositories

import configurations.IAppEnv
import org.koin.core.component.KoinComponent
import org.ktorm.database.Database

interface IRepositoryBase: KoinComponent {
    val appEnv: IAppEnv
    val database: Database
}