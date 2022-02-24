package integrationTests.compositions.headers

import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class HeaderCompositionsIT: BehaviorSpecIT({

    given("Header compositions") {

        suspend fun setupCreateComposition(): Triple<Int, Int, Int> {
            TODO()
        }

        and("create publicly viewable of all variants of composition") {

            then("get layout of compositions") {
                rollback {

                }
            }

            then("Update composition") {
                rollback {

                }
            }

            then("Delete composition") {
                rollback {

                }
            }
        }
    }

})