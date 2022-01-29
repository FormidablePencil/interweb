package com.idealIntent.configurations

import io.ktor.config.*
import org.ktorm.database.Database
import java.util.*

/**
 * Application environment variables and DB access
 *
 * @property appConfig Environment variables
 * @property dbConnection Database credentials
 * @property database Access to database
 */
data class AppEnv(
    private val appConfig: ApplicationConfig, // todo - should deprecate public in place of private
    private val dbConnection: Properties,
) {
    val database: Database = Database.connect(
        dbConnection.getProperty("jdbcUrl"),
        user = dbConnection.getProperty("username"),
        password = dbConnection.getProperty("password"),
    )

    /**
     * Get config
     *
     * @param path Path to environment variable
     * @return environment variable
     */
    fun getConfig(path: String): String {
        return appConfig.property(path).getString()
    }
}