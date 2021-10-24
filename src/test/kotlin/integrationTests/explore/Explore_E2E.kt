package integrationTests.explore

import domainServices.AuthorsPortfolioDomainService
import domainServices.ExploreDomainService
import domainServices.ThreadDomainService
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import models.Author
import org.junit.Assert
import integrationTests.signup.SignupFlows
import integrationTests.thread.CreateThreadFlows

class Explore_E2E(
    private val threadDomainService: ThreadDomainService,
    private val exploreDomainService: ExploreDomainService,
    private val authorsPortfolioDomainService: AuthorsPortfolioDomainService,
    private val signupE2E: SignupFlows,
    val createThreadE2E: CreateThreadFlows
) {
    fun VisitAuthorsPortfolio_userflow() {
        //region setup
        var result = CreateAuthorRequest("", "", "", "", "")
        val signupResult: SignupResult = signupE2E.Signup_flow(result)
        threadDomainService.createThread(signupResult.authorId)
        //endregion

        //region actions
        // Search author
        val author: Author = exploreDomainService.SearchAuthors(result.email)
        // click author which gets author's layouts
        val authorsLayouts = authorsPortfolioDomainService.GetAuthorsLayouts(author.id)
        val layoutChoseToView = authorsLayouts.first()
        // then choose author's layout to view
        val authorsLayout = authorsPortfolioDomainService.GetLayout(author.id, layoutChoseToView)

        Assert.assertTrue(authorsLayout.layoutComponents.count() > 0)
        Assert.assertTrue(authorsLayout.layoutArrangement.count() > 0)
        //endregion
    }

    fun VisitThread_userflow() {
        //region setup
        val threadId = createThreadE2E.CreateThread()
        //endregion

        var threads = exploreDomainService.SearchThreadsByTitle("Theology")

        var thread = threads.first()
    }
}