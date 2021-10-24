import configurations.DIHelper
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import repositories.IAuthorRepository

import org.koin.test.KoinTestRule
import org.koin.test.inject

class SomeOtherTest : KoinTest {
    val authorRepository by inject<IAuthorRepository>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(DIHelper.CoreModule)
    }

    @Test
    fun testDbConnection() {
    }
}