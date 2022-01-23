package integrationTests.space.tests

import integrationTests.auth.flows.LoginFlow
import integrationTests.space.flows.SpaceFlow
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT

class SpaceIT : BehaviorSpecIT({
    val spaceFlow: SpaceFlow by inject()

    Given("create") {
        spaceFlow

    }

    Given("read") {

    }

    Given("update") {

    }

    Given("soft delete") {
    }
})