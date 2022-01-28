package com.idealIntent.repositories.profile

import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class AccountRepositoryTest : BehaviorSpecUtRepo({

    given("getAssortmentById") { }

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
