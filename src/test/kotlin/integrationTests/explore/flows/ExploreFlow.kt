package integrationTests.explore.flows

import com.idealIntent.services.AuthorsPortfolioService
import com.idealIntent.services.ExploreService
import com.idealIntent.services.SpaceService
import integrationTests.auth.flows.SignupFlow
import com.idealIntent.dtos.CreateAuthorRequest

class ExploreFlow(
    private val threadDomainService: SpaceService,
    private val exploreDomainService: ExploreService,
    private val authorsPortfolioDomainService: AuthorsPortfolioService,
    private val signupFlows: SignupFlow,
) {
    suspend fun VisitAuthorsPortfolio_userflow() {
        TODO()

        //region setup
        val createAuthorRequest = CreateAuthorRequest(
            "username", "email", "firstname",
            "lastname", "password"
        )

        val signupResult = signupFlows.signup(createAuthorRequest)
//       if (signupResult?.authorId == null)

//        threadDomainService.createThread(signupResult.authorId!!)
        //endregion

        //region actions
        // Search author
//        val author: Author = exploreDomainService.SearchAuthors(createAuthorRequestExistentInDb.email)
//        // click author which gets author's layouts
//        val authorsLayouts = authorsPortfolioDomainService.GetAuthorsLayouts(author.id)
//        val layoutChoseToView = authorsLayouts.first()
//        // then choose author's layout to view
//        val authorsLayout = authorsPortfolioDomainService.GetLayout(author.id, layoutChoseToView)
//
//        Assert.assertTrue(authorsLayout.layoutComponents.count() > 0)
//        Assert.assertTrue(authorsLayout.layoutArrangement.count() > 0)
//        //endregion
    }

    fun VisitThread_userflow() {
        TODO()
        //region setup
        //endregion

        var threads = exploreDomainService.SearchThreadsByTitle("Theology")

        var thread = threads.first()
    }
}