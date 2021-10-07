package thread

import domainServices.ThreadDomainService

class Thread_E2E(
    private val threadDomainService: ThreadDomainService
) {
    fun SearchedThreads_userflow() {
        //region
        
        // create threads first
        var threadId = 1
        
        //endregion
        
        threadDomainService.GetThreadById(threadId)
    }

    fun AuthorsPortfolioThread_userflow() {

    }

    fun RelatedThread_workflow() {
        // need to first navigate to thread first

    }

    fun SubThread_workflow() {
        // need to first navigate to thread first

    }
}