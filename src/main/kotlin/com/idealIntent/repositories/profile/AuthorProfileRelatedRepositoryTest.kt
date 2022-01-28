package com.idealIntent.repositories.profile

import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import com.idealIntent.serialized.CreateAuthorRequest
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.SqlConstraint
import shared.testUtils.rollback

class AuthorProfileRelatedRepositoryTest : BehaviorSpecUtRepo({
    val authorProfileRelatedRepository: AuthorProfileRelatedRepository by inject()

    fun genReq(
        username: String = "username123",
        email: String = "email",
        firstname: String = "firstname",
        lastname: String = "lastname",
        password: String = "password"
    ) = CreateAuthorRequest(
        username = username, email = email, firstname = firstname, lastname = lastname, password = password
    )

    beforeTest {
    }

    xgiven("createNewAuthor") {

    }

    given("getAuthorWithDetail") {
        then("should have joined table authors with author_details") {
            rollback {
                // todo
                //  author and author_detail must be created first
                val response = authorProfileRelatedRepository.getAuthorWithDetail(394)
                response shouldNotBe null
            }
        }
    }

    given("getAuthorWithDetailAndAccount") {
        then("should have joined table authors and accounts with author_details") {
            rollback {
                // todo
                //  author and author_detail and account must be created first
                val response = authorProfileRelatedRepository.getAuthorWithDetailAndAccount(394)
                response shouldNotBe null
            }
        }
    }

    given("constraints") {
        whenConstraint(SqlConstraint.Unique, "username", null) {
            authorProfileRelatedRepository.createNewAuthor(genReq(username = "username", email = "someEmail"))
        } // todo - create validation with valikator

        whenConstraint(SqlConstraint.Unique, "email", null) {
            authorProfileRelatedRepository.createNewAuthor(genReq(email = "email", username = "someUsername"))
        }

        whenConstraint(SqlConstraint.MaxSize, "email", 60) { x ->
            authorProfileRelatedRepository.createNewAuthor(genReq(email = x))
        }

        whenConstraint(SqlConstraint.MaxSize, "username", 15) { x ->
            authorProfileRelatedRepository.createNewAuthor(genReq(username = x))
        }

        // failing and passing attempt with repository against sql db

        // failing and passing against http Request model/dto - they use valikator

        // this way no data going in from http doesn't succeed and neither one of use devs don't accidentally attempt to insert the wrong stuff
        //  - at least the console will tell you that you fucked up when creating integration tests for a functionality.
    }
    // todo - unit test sql's column type constraints
})
