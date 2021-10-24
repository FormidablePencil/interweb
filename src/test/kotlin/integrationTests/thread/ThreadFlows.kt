package integrationTests.thread

import org.koin.test.KoinTest
import org.koin.test.inject

class ThreadFlows : KoinTest {
    private val createThreadFlows by inject<CreateThreadFlows>()

    fun NavigateToGetRelatedThread_workflow() {
        //region setup
        //endregion

        // need to first navigate to thread first

    }

    fun NavigateToSubThread_workflow() {
        // need to first navigate to thread first

    }
}