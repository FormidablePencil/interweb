package integrationTests.compositions.texts

import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class TextCompositionsIT : BehaviorSpecIT({

    given("Simple text compositions") {

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