package repositories.components

import dtos.libOfComps.genericStructures.Text
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class TextRepositoryTest : BehaviorSpecUtRepo() {
    private val textRepository = TextRepository()

    init {
        given("insertCollectionOfTexts") {
            then("should have inserted a collection of random texts") {
                rollback {
                    textRepository.insertCollectionOfTexts(
                        listOf(
                            Text(orderRank = 10000, text = "space"),
                            Text(orderRank = 20000, text = "people"),
                            Text(orderRank = 30000, text = "man"),
                        ),
                        "random texts"
                    )
                    textRepository.getCollectionOfTextsById(

                    )
                }
            }
        }
    }
}
