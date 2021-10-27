package repositories

import configurations.IAppEnv
import configurations.IConnectionToDb
import org.koin.core.component.KoinComponent
import org.ktorm.database.Database

interface IRepositoryBase: IConnectionToDb {
}