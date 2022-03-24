package shared.testUtils

import com.idealIntent.configurations.DIHelper
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import shared.DITestHelper

/** Unit testing com.idealIntent.repositories class. */
abstract class BehaviorSpecUtRepo(body: BehaviorSpecUtRepo.() -> Unit = {}) : BehaviorSpecIT(), SqlColConstraint {
    init {
        body()
    }
}

//abstract class BehaviorSpecUtRepoWithQueryRowSet : BehaviorSpecUtRepo() {
//    val compInstance = BannerImageModel
//    val compSource = CompositionSourceRepository.compSource
//    val compSource2Layout = CompositionSourceRepository.compSource2Layout
//    val prvAth2CompSource = CompositionSourceRepository.prvAth2CompSource
//    val compInstance2compSource = CompositionSourceRepository.compInstance2compSource
//    val author = AuthorProfileRelatedRepository.author
//
//    val row: QueryRowSet = mockk()
//
//    init {
//        every { row[compInstance.id]!! } returns 1
//        every { row[compSource.id]!! } returns 2
//        every { row[compSource.privilegeLevel]!! } returns 3
//        every { row[compInstance.imageUrl]!! } returns "a"
//        every { row[compInstance.imageAlt]!! } returns "b"
//        every { row[compSource.name]!! } returns "c"
//    }
//}

abstract class BehaviorSpecUtRepo2(body: BehaviorSpecUtRepo2.() -> Unit = {}) : BehaviorSpecIT(), SqlColConstraint {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        body()
    }
}
