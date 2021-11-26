package shared.testUtils

/** Rollback transaction wrapper for repository unit tests which call the database and integration tests. One limitation
 *  is you cannot test inserting duplicate data because of how db transactions work. */
suspend fun <T> DoHaveDbConnection.rollback(cleanup: Boolean = true, code: suspend () -> T): T {
    if (cleanup)
        appEnv.database.useTransaction {
            val result = code()
            appEnv.database.transactionManager.currentTransaction?.rollback()
            return result
        }
    else {
        return code()
    }
}