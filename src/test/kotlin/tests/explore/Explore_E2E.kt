package tests.explore

import dto.signup.SignupResWF
import domainServices.AuthorsPortfolioDomainService
import domainServices.ExploreDomainService
import domainServices.ThreadDomainService
import models.Author
import org.junit.Assert
import tests.signup.Signup_E2E
import tests.thread.CreateThread_E2E

class Explore_E2E(
    private val threadDomainService: ThreadDomainService,
    private val exploreDomainService: ExploreDomainService,
    private val authorsPortfolioDomainService: AuthorsPortfolioDomainService,
    private val signupE2E: Signup_E2E,
    val createThreadE2E: CreateThread_E2E
) {
    fun VisitAuthorsPortfolio_userflow() {
        //region setup
        val signupResWF: SignupResWF = signupE2E.Signup_flow()
        threadDomainService.CreateThread(signupResWF.authorId)
        //endregion

        //region actions
        // Search author
        val author: Author = exploreDomainService.SearchAuthors(signupResWF.username)
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
        val threadId = createThreadE2E.CreateThread_workflow()
        //endregion

        var threads = exploreDomainService.SearchThreadsByTitle("Theology")

        var thread = threads.first()
    }
}