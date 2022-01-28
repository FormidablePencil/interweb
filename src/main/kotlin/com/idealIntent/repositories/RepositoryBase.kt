package com.idealIntent.repositories

import com.idealIntent.configurations.AppEnv
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database

open class RepositoryBase : KoinComponent {
    private val appEnv: AppEnv by inject()
    val database: Database = appEnv.database
}