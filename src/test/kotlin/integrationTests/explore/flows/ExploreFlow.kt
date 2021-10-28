package integrationTests.explore.flows

import domainServices.AuthorsPortfolioDomainService
import domainServices.ExploreDomainService
import domainServices.ThreadDomainService
import dtos.author.CreateAuthorRequest
import integrationTests.signup.flows.SignupFlow
import models.profile.Author
import org.junit.Assert

class ExploreFlow(
    private val threadDomainService: ThreadDomainService,
    private val exploreDomainService: ExploreDomainService,
    private val authorsPortfolioDomainService: AuthorsPortfolioDomainService,
    private val signupFlows: SignupFlow,
) {
    fun VisitAuthorsPortfolio_userflow() {
        //region setup
        val createAuthorRequest = CreateAuthorRequest(
            "username", "email", "firstname",
            "lastname", "password"
        )

        val signupResult = signupFlows.signup(createAuthorRequest)
       if (signupResult?.authorId == null)
           return

        threadDomainService.createThread(signupResult.authorId!!)
        //endregion

        //region actions
        // Search author
        val author: Author = exploreDomainService.SearchAuthors(createAuthorRequest.email)
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
        //endregion

        var threads = exploreDomainService.SearchThreadsByTitle("Theology")

        var thread = threads.first()
    }
}