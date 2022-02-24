package integrationTests.compositions.grids

import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class GridCompositionsIT : BehaviorSpecIT({

    given("Grid compositions") {

        suspend fun setupCreateComposition(): Triple<Int, Int, Int> {
            TODO()
        }

        and("create publicly viewable of all compositions under a layout") {

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