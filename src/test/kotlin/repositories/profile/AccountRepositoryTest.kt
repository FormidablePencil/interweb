package repositories.profile

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class AccountRepositoryTest : BehaviorSpecUtRepo({

    given("getById") { }

    given("getByEmail") {
        then("get by email") {
            rollback {
                val accountRepository = AccountRepository()
                val f = accountRepository.getByEmail("email@gmail.com")
                println(f)
            }
        }
    }
})
