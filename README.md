# Testing
There are
- Unit tests
- Integration tests (try not to access services directly but through user flows)
- user flows

### Structure of unit tests example
- services
    - managers
      - tokenManager - name mimicking file
        - GenerateTokens.kt name mimicking class method

### Structure of integration tests
- Feature(dir)
    - Flows(dir)
        - file name ends with Flows.kt
    - IntegrationTests(dir)
        - file name ends with IT.kt