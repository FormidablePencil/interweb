# Testing
There are unit tests, integration tests and user flows.

#### Why integration tests and unit test?
- Allows you to forget and make mistakes. The tests got you covered.

#### Why unit tests?
- To help the developer to secure each unit of code.
- To preserve the functionality later when code needs to be updated. (If test fails, the you broke the functionality).
- Documentation.
- Run to test the whole codebase with a click of a button.

#### Why integration tests?
- Testing the integration of code - e2e style.
- Document feature flows.

#### Why validate sql column constraints in repository unit testing?
- To keep http requests body validating in sync with sql column constraints. If column constraints changes then the request-body-validation will have to change, and if req-body-valid is not validating that the data matches the same constraints as in the db col then the test should fail given that there is one to make sure these two things are in sync.

## Backend code structures

### Structure of unit tests  <!-- {docsify-ignore} -->
- services
    - managers
        - tokenManager - name mimicking file
            - GenerateTokens.kt name mimicking class method

### Structure of integration tests  <!-- {docsify-ignore} -->
- Feature(dir)
    - Flows(dir)
        - file name ends with Flows.kt
    - IntegrationTests(dir)
        - file name ends with IT.kt


## Frontend code structure

- Networking integration tests
    - No mocking networking itself since there should be a very little amount of code in them.
- Unit testing UI
- Unit testing functionality code (like helpers and various other logic)
- E2E testing & flows

## Frontend testing

- E2E testing with playwright
- snapshots with enzyme
- unit testing pure logic with jest
- unit testing the state of a component with enzyme

### Playwright vs Jest

The difference between these them is that Jest is for unit testing with a lot of functionality mocked 
including http networking whereas PlayWright is for E2E which queries the database through http call to the server.
