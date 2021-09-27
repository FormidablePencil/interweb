import configurations.DIHelper
import org.junit.Rule
import org.junit.Test
import org.koin.core.scope.get
import org.koin.dsl.module
import org.koin.test.KoinTest
import repositories.AuthorRepository
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
//        AuthorRepository().CreateAuthor("dfd")
        authorRepository.CreateAuthor("New user");
    }
}