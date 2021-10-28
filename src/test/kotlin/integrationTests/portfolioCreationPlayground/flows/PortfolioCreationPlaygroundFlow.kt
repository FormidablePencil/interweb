package integrationTests.portfolioCreationPlayground.flows

import domainServices.PortfolioCreationPlaygroundDomainService
import dtos.portfolio.AddNewComponentRequest
import org.koin.test.KoinTest
import org.koin.test.inject

class PortfolioCreationPlaygroundFlow: KoinTest {
    private val portfolioCreationPlaygroundDomainService by inject<PortfolioCreationPlaygroundDomainService>()

    fun getLayout() {
        //region setup

        // create layout with components
        val layoutId = 1

        //endregion

        portfolioCreationPlaygroundDomainService.GetLayout(layoutId)
    }

    fun editPortfolio() {
        //region setup
        // authors have foreign key of componentLibrary.Id -> libraryName id -> component.libraryId
        // a component will have a column that tells you to what "library" it belongs to
        addNewComponent()
        //endregion

        //region action
        portfolioCreationPlaygroundDomainService.GetLibraryOfAllComponents() // gets all components with foreign key of libraryId
        //region action

    }

    // do you want to update the db for component one at a time or all at once?? one at a time for users to get back to where they left off
    fun addNewComponent(): Int {
        //region param (thought)
        // They choose to design for what Layout, e.g. Default, Web dev, photographer, ect.
        var portfolioLayoutId = 2
        var libraryId = 2

        // choose an array of components to choose from
        //picked: carousel, pngBuilder

        // all the layouts are in the frontend. The backend just keeps track of users arrangements and choices to use
        /// We should have a table of layouts - carousel, basicImage, your "groupings", specific postings in groupings
        /// We could have a table to keep track of arrangement components e.g. comp id 122 - is carousel, comp id 32 - is basicImage. This is an array of comp ids

        //endregion

        //region operations
        var componentId = portfolioCreationPlaygroundDomainService.AddNewComponent(AddNewComponentRequest(portfolioLayoutId, libraryId))

        // insert component to db to some table and retrieve the generated id (componentId) for later use
        // Get Portfolio column="order" which is an array of component ids and...
        // mutate the array by inserting id in between the componentIds that it belongs

        //endregion

        return componentId
    }

    // done in portfolio page drag mode
    fun updateArrangmentOfComponent() {
        //region setup and thought
        var componentArrangment = listOf<Int>(1, 12, 43, 13)
        var layoutId = 1

        // when users drags the components around and finally clicks save then
        // updateArrangementOfComponents is triggered

        //endregion

        //region action
        portfolioCreationPlaygroundDomainService.UpdateLayoutArrangment(layoutId, componentArrangment)
        //endregion

        //region layoutArrangement has changed
        portfolioCreationPlaygroundDomainService.GetLayoutArrangment(layoutId)
        //endregion
    }

    // when user saves in component canvas page, updateComponent is triggered
    fun updateComponent() {
        //region setup
        var componentId = addNewComponent();
        //endregion

        //region actions
        portfolioCreationPlaygroundDomainService.UpdateComponent(componentId)
        //endregion
    }

    // prompt if user is sure that they want to delete
    fun DeleteComponent_workflow() {
        //region setup
        var componentId = addNewComponent();
        //endregion

        //region actions
        portfolioCreationPlaygroundDomainService.DeleteComponent(componentId)
        //endregion
    }

    // will get you all your components through author's library table -> through column foreign id
}