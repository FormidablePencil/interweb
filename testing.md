# Testing
There are unit tests, integration tests and user flows.

Why integration tests and unit test?
- Allows you to forget and make mistakes. The tests got you covered.

Why unit tests?
- To help the developer to secure each unit of code.
- To preserve the functionality later when code needs to be updated. (If test fails, the you broke the functionality).
- Documentation.
- Run to test the whole codebase with a click of a button.

Why integration tests?
- Testing the integration of code - e2e style.
- Document feature flows.

Why validate sql column constraints in repository unit testing?
- To keep http requests body validating in sync with sql column constraints. If column constraints changes then the request-body-validation will have to change, and if req-body-valid is not validating that the data matches the same constraints as in the db col then the test should fail given that there is one to make sure these two things are in sync.

Structure of unit tests example
- services
    - managers
        - tokenManager - name mimicking file
            - GenerateTokens.kt name mimicking class method

Structure of integration tests
- Feature(dir)
    - Flows(dir)
        - file name ends with Flows.kt
    - IntegrationTests(dir)
        - file name ends with IT.kt
