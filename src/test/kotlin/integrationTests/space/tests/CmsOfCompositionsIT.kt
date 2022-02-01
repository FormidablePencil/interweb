package integrationTests.space.tests

import shared.testUtils.BehaviorSpecIT

class CmsOfCompositionsIT : BehaviorSpecIT() {
//    private val cmsLibOfCompsFlow = CmsLibOfCompsFlow()
//    private val spaceService: SpaceService by inject()
//
//    init {
//        val compositions = listOf(
//            UserComposition(compositionType = CompositionCategory.Carousel, jsonData = ""),
//            UserComposition(compositionType = CompositionCategory.Banner, jsonData = ""),
//            UserComposition(compositionType = CompositionCategory.Text, jsonData = "")
//        )
//        given("test that all components of composition work") {
//            compositions.forEach {
//                then("create a single component ${it.compositionType.name}") {
//                    rollback {
//                        val spaceAddress = ""
//
//                        val createCompRes = cmsLibOfCompsFlow.createComposition(
//                            CreateCompositionRequest(spaceAddress, userComposition = it)
//                        )
//                        createCompRes shouldBe true
//                        val result = spaceService.getSpaceByAddress(GetSpaceRequest(address = spaceAddress))
////                    result.
//                    }
//                }
//            }
//        }
//
//        given("create multiple components") {
//            then("success") {
//                rollback {
//
//                }
//            }
//        }
//    }
}