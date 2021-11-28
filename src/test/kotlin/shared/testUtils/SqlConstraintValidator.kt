package shared.testUtils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerContext

enum class SqlConstraint {
    MaxSize,
    Unique,
    NotNull,
    //    MinSize,
    //    EmptyString; // todo - define how each is tested for violation and non-violation.
}

/** To validate sql constraints. */
// todo - write a test for this
interface SqlColConstraint : DoesHaveAppEnv {
    /** test both scenarios, with a violating column constraint and without. */
    suspend fun BehaviorSpecGivenContainerContext.whenConstraint(
        constraint: SqlConstraint,
        columnName: String,
        sizeLimit: Int? = null,
        test: suspend (generatedString: String) -> Unit,
    ) {
        runScenario(constraint, columnName, sizeLimit, test, true)
        runScenario(constraint, columnName, sizeLimit, test, false)
    }

    /** Handle code that's expected to violation constraint and handle the code that doesn't. */
    private suspend fun BehaviorSpecGivenContainerContext.runScenario(
        constraint: SqlConstraint,
        columnName: String,
        sizeLimit: Int?,
        test: suspend (generatedString: String) -> Unit,
        violateConstraint: Boolean
    ) {
        // region Naming the test
        val showLimit: String = if (constraint == SqlConstraint.MaxSize) sizeLimit.toString() else ""
        val expectedFailureString = if (violateConstraint) "FAIL" else "SUCCEED"
        val isOverString = if (violateConstraint) "OVER" else ""
        val testName = "${getName(constraint)} $showLimit $columnName $isOverString should $expectedFailureString"
        // endregion

        val genString = generateString(sizeLimit, violateConstraint)

        when (constraint) {
            SqlConstraint.Unique ->
                if (violateConstraint) handleConstrainViolation(testName) { test(genString); test(genString) } // Run twice if database as wiped
            SqlConstraint.MaxSize ->
                if (sizeLimit !is Int) throw Exception("size limit can not be null when testing for ${SqlConstraint.MaxSize}")
                else if (violateConstraint) handleConstrainViolation(testName) { test(genString) }
                else handleNoConstrainViolation(testName) { test(genString) }
            SqlConstraint.NotNull ->
                if (violateConstraint) handleConstrainViolation(testName) { test(genString) }
                else handleNoConstrainViolation(testName) { test(genString) }
        }
    }

    /** If sql insert query is expected to fail then validate that it does with a try catch and just in case that
     * it succeeds rollback the database before the insertion. */
    private suspend fun BehaviorSpecGivenContainerContext.handleConstrainViolation(
        testName: String,
        callback: suspend () -> Unit
    ) {
        Then(testName) {
            rollback {
                shouldThrow<Exception> {
                    callback()
                }
            }
        }
    }

    /** If sql insert query is expected to succeed then we better revert the database back if we were to reuse the test.*/
    private suspend fun BehaviorSpecGivenContainerContext.handleNoConstrainViolation(
        testName: String,
        callback: suspend () -> Unit
    ) {
        Then(testName) {
            rollback {
                callback()
            }
        }
    }

    /** Used for testing character max limit constraint of a column. Increment 1 over the size limit if constraint violation is expected. */
    private fun generateString(sizeLimit: Int?, violateConstraint: Boolean): String {
        if (sizeLimit !is Int) return ""
        var string = ""
        for (num in 1..sizeLimit) string += "$"
        return if (violateConstraint) "$string+" else string
    }

    private fun getName(constraint: SqlConstraint): String {
        return when (constraint) {
            SqlConstraint.MaxSize -> "MAX-SIZE";
            SqlConstraint.Unique -> "UNIQUE";
            SqlConstraint.NotNull -> "NOT-NULL"
        }
    }
}