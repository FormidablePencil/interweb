package repositories.interfaces

import org.koin.core.component.KoinComponent
import org.ktorm.database.Database

interface IRepositoryBase: KoinComponent {
    val database: Database
}