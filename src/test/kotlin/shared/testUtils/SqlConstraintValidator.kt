package shared.testUtils

import configurations.interfaces.IConnectionToDb
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerContext

enum class SqlConstraint {
    SizeLimit, Unique, NotNull;
}

interface DoHaveDbConnection {
    val connectionToDb: IConnectionToDb
}

/** To validate sql constraints. */
interface SqlColConstraint : DoHaveDbConnection {
    suspend fun BehaviorSpecGivenContainerContext.whenConstraint(
        constraint: SqlConstraint,
        columnName: String,
        sizeLimit: Int,
        test: suspend (generatedString: String) -> Unit,
    ) {
        executions(constraint, columnName, sizeLimit, test)
    }

    private suspend fun BehaviorSpecGivenContainerContext.executions(
        constraint: SqlConstraint, columnName: String, sizeLimit: Int, test: suspend (generatedString: String) -> Unit
    ) {
        // test both scenarios
        run(constraint, columnName, sizeLimit, test, true)
        run(constraint, columnName, sizeLimit, test, false)
    }

    private suspend fun BehaviorSpecGivenContainerContext.run(
        constraint: SqlConstraint,
        columnName: String,
        sizeLimit: Int,
        test: suspend (generatedString: String) -> Unit,
        expectedFailure: Boolean
    ) {
        val genString = generateString(sizeLimit)
        var showLimit: Int? = null
        if (constraint == SqlConstraint.SizeLimit) showLimit = sizeLimit
        val expectedFailureString = if (expectedFailure) "fail" else "succeed"
        val testName = "${getName(constraint)} on $columnName $showLimit should $expectedFailureString"

        when (constraint) {
            SqlConstraint.Unique ->
                if (expectedFailure) handleFail(testName) { test(genString); test(genString) } // Run twice if database as wiped
            SqlConstraint.SizeLimit ->
                if (expectedFailure) handleFail(testName) { test(genString) }
                else handleSuccess(testName) { test(genString) }
            SqlConstraint.NotNull ->
                if (expectedFailure) handleFail(testName) { test(genString) }
                else handleSuccess(testName) { test(genString) }
        }

    }

    private suspend fun BehaviorSpecGivenContainerContext.handleFail(
        testName: String,
        callback: suspend () -> Unit
    ) {
        Then(testName) {
            rollback {
                shouldThrow<Exception> {
                    callback()
                    callback()
                }
            }
        }
    }

    private suspend fun BehaviorSpecGivenContainerContext.handleSuccess(
        testName: String,
        callback: suspend () -> Unit
    ) {
        Then(testName) {
            rollback {
                callback()
            }
        }
    }

    private fun generateString(characters: Int): String {
        var string = "$"
        for (num in 0..characters) string += "$"
        return string
    }

    private fun getName(constraint: SqlConstraint): String {
        return when (constraint) {
            SqlConstraint.SizeLimit -> "varchar";
            SqlConstraint.Unique -> "unique";
            SqlConstraint.NotNull -> "not null"
        }
    }
}