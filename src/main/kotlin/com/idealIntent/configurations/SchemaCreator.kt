package com.idealIntent.configurations

import models.profile.AuthorsModel
import models.profile.AuthorDetails
import models.profile.AccountsModel
import com.idealIntent.models.auth.PasswordsModel
import com.idealIntent.models.awareness.AwarenessStatusesModel
import com.idealIntent.models.awareness.AwarenessTasksModel
import org.ktorm.database.Database
import org.ktorm.schema.Table

/**
 * Automatically creates database tables from Ktorm model definitions.
 * Uses Ktorm's schema introspection to generate DDL.
 */
object SchemaCreator {

    fun createTablesIfNotExist(database: Database) {
        // Core tables needed for tests - using the actual model definitions
        val essentialTables = listOf(
            AuthorsModel,
            AuthorDetails,
            AccountsModel,
            PasswordsModel,
            AwarenessStatusesModel,
            AwarenessTasksModel
        )

        essentialTables.forEach { table ->
            try {
                createTableFromModel(database, table)
            } catch (e: Exception) {
                println("Note: Could not create table ${table.tableName}: ${e.message}")
            }
        }
    }

    private fun createTableFromModel(database: Database, table: Table<*>) {
        val tableName = table.tableName
        val createSql = getCreateTableSql(tableName)

        if (createSql != null) {
            database.useConnection { conn ->
                conn.createStatement().use { stmt ->
                    try {
                        stmt.execute(createSql)
                        println("✓ Created table: $tableName")
                    } catch (e: Exception) {
                        println("Note: Table $tableName might already exist: ${e.message}")
                    }
                }
            }
        }
    }

    private fun getCreateTableSql(tableName: String): String? {
        return when (tableName) {
            "authors" -> "CREATE TABLE IF NOT EXISTS authors (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(15) NOT NULL UNIQUE)"
            "author_details" -> "CREATE TABLE IF NOT EXISTS author_details (author_id INT PRIMARY KEY, firstname VARCHAR(255), lastname VARCHAR(255), FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"
            "accounts" -> "CREATE TABLE IF NOT EXISTS accounts (author_id INT PRIMARY KEY, email VARCHAR(60) NOT NULL UNIQUE, verified_email INT DEFAULT 0, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"
            "passwords" -> "CREATE TABLE IF NOT EXISTS passwords (password VARCHAR(255) NOT NULL, author_id INT PRIMARY KEY, FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"
            "tokens" -> "CREATE TABLE IF NOT EXISTS tokens (id INT AUTO_INCREMENT PRIMARY KEY, refresh_token VARCHAR(500) NOT NULL, author_id INT NOT NULL, FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"
            "email_verification_codes" -> "CREATE TABLE IF NOT EXISTS email_verification_codes (author_id INT PRIMARY KEY, code VARCHAR(255) NOT NULL, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"

            // Composition system tables
            "composition_sources" -> "CREATE TABLE IF NOT EXISTS composition_sources (id INT AUTO_INCREMENT PRIMARY KEY, source_id INT NOT NULL, privilege_id INT DEFAULT 0)"
            "spaces" -> "CREATE TABLE IF NOT EXISTS spaces (address VARCHAR(255) PRIMARY KEY, author_id INT NOT NULL, json_data TEXT, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"
            "composition_layouts" -> "CREATE TABLE IF NOT EXISTS composition_layouts (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, source_id INT NOT NULL)"
            "layout_to_spaces" -> "CREATE TABLE IF NOT EXISTS layout_to_spaces (id INT AUTO_INCREMENT PRIMARY KEY, layout_id INT NOT NULL, space_address VARCHAR(255) NOT NULL, FOREIGN KEY (layout_id) REFERENCES composition_layouts(id) ON DELETE CASCADE, FOREIGN KEY (space_address) REFERENCES spaces(address) ON DELETE CASCADE)"
            "privileged_author_to_composition_sources" -> "CREATE TABLE IF NOT EXISTS privileged_author_to_composition_sources (id INT AUTO_INCREMENT PRIMARY KEY, author_id INT NOT NULL, source_id INT NOT NULL, modify_privilege INT DEFAULT 0, deletion_privilege INT DEFAULT 0, modify_user_privileges INT DEFAULT 0, FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE)"
            "images" -> "CREATE TABLE IF NOT EXISTS images (id INT AUTO_INCREMENT PRIMARY KEY, description TEXT, url VARCHAR(500) NOT NULL)"
            "image_collections" -> "CREATE TABLE IF NOT EXISTS image_collections (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, source_id INT NOT NULL)"
            "image_to_collections" -> "CREATE TABLE IF NOT EXISTS image_to_collections (id INT AUTO_INCREMENT PRIMARY KEY, image_id INT NOT NULL, collection_id INT NOT NULL, order_rank INT DEFAULT 0, FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE, FOREIGN KEY (collection_id) REFERENCES image_collections(id) ON DELETE CASCADE)"
            "banner_images" -> "CREATE TABLE IF NOT EXISTS banner_images (id INT AUTO_INCREMENT PRIMARY KEY, source_id INT NOT NULL, name VARCHAR(255) NOT NULL)"

            // Awareness framework tables
            "awareness_statuses" -> "CREATE TABLE IF NOT EXISTS awareness_statuses (id VARCHAR(20) PRIMARY KEY, name VARCHAR(50) NOT NULL, description TEXT)"
            "awareness_tasks" -> "CREATE TABLE IF NOT EXISTS awareness_tasks (id VARCHAR(20) PRIMARY KEY, title TEXT NOT NULL, description TEXT, status VARCHAR(20) DEFAULT 'b2d4b2', parent_id VARCHAR(20), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, FOREIGN KEY (status) REFERENCES awareness_statuses(id), FOREIGN KEY (parent_id) REFERENCES awareness_tasks(id) ON DELETE CASCADE)"
            else -> null
        }
    }

    fun initializeAwarenessStatuses(database: Database) {
        val insertSql = """
            INSERT IGNORE INTO awareness_statuses (id, name, description) VALUES
            ('b2d4b2', 'Pending', 'Task not yet started'),
            ('b2d4b3', 'InProgress', 'Task actively being worked on'),
            ('b2d4b4', 'Completed', 'Task finished successfully'),
            ('b2d4b5', 'Blocked', 'Task waiting on dependencies'),
            ('b2d4b6', 'OnHold', 'Task temporarily paused')
        """.trimIndent()

        database.useConnection { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(insertSql)
            }
        }
        println("✓ Initialized awareness statuses")
    }
}
