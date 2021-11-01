package shared.testUtils

import configurations.interfaces.IConnectionToDb

/** Extends integration testing class and unit testing repository class */
interface IRollback {
    val connectionToDb: IConnectionToDb?
}

/** Rollback transaction wrapper for integration testing. */
fun <T> IRollback.rollbackDeprecated(cleanup: Boolean = true, code: () -> T): T {
    connectionToDb ?: throw Exception("rollbackDeprecated fun failed")
    if (cleanup)
        connectionToDb!!.database.useTransaction {
            val result = code()
            connectionToDb!!.database.transactionManager.currentTransaction?.rollback()
            return result;
        }
    else {
        return code()
    }
}

/** Rollback transaction wrapper for repository unit tests which call the database and integration tests. One limitation
 *  is you cannot test inserting duplicate data because of how db transactions work. */
suspend fun <T> IRollback.rollback(cleanup: Boolean = true, code: suspend () -> T): T {
    connectionToDb ?: throw Exception("rollbackDeprecated fun failed")
    if (cleanup)
        connectionToDb!!.database.useTransaction {
            val result = code()
            connectionToDb!!.database.transactionManager.currentTransaction?.rollback()
            return result;
        }
    else {
        return code()
    }
}