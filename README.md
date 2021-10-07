# Done
- PortfolioCreationPlayground test/blueprint has been done and now ready for implementation for the most part
- 


// TODO: Submodules (dev-ops) but first... why TP.Core.Web, the mediary between Core and TP.WebApi? - Because
TP.Functions needed most of the same logic as TP.WebApi

// TODO: These structures will be implemented later when I make more leeway

# Codebase

## Projects and structure

### Structure of IW.Core (we combined Data with Core)
Repositories -
    db querying

DTOs

Models

SharedManagers -
    Shared code across platforms

### Structure of IW.Core.WebAPIs && IW.Core.MobileAPIs
routes/controllers -
    Will be renamed and extracted out to separate proj at some point. Routes in API projects for http calls,
    typically for frontend purposes

DomainServices -
    Outer most layer for the API projects to consume

Managers -
    Extracted out logic for multiple DomainServices. (rather than needing to DI large DomainService classes).

# Automated tests

e2e, checkpoints and unit tests

These tests will lie in their respective projects

## IW.UtilLib4Tests will exist for utilities and tests to consume
Shortcuts -
    Parts of code skipped for producing changes in db for testing purposes by mocking dependencies.

GenericFakeData

DependencyInjectionHelper
    Inject mocks from mockFactories, mocked log repositories and perhaps classes from other projects.

TODO: TP way was having a startup for every lib which worked since tests were their own project. IW however,
I need to understand where the startup of the tests is.


[comment]: <> (The tests will mimic the codebase structure)

[comment]: <> (## Projects and structure)

[comment]: <> (IW.{proj}.UnitTests -)

[comment]: <> (    dependents of each unit of code is mocked)

[comment]: <> (IW.{proj}.E2E -)

[comment]: <> (    end to end &#40;integration testing&#41;)

[comment]: <> (IW.{proj}.E2ECheckpoints - )

[comment]: <> (    Consumed by E2E only)

[comment]: <> (IW.{proj}.UtilLib4Tests - )

[comment]: <> (    Consumed by testing and utility projects)

