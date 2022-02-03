package com.idealIntent.repositories.profile

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.CreateAuthorRequest
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.SqlConstraint
import shared.testUtils.rollback

class AuthorProfileRelatedRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val authorProfileRelatedRepository: AuthorProfileRelatedRepository by inject()
    private val authorRepository: AuthorRepository by inject()
    private val signupFlow: SignupFlow by inject()
    private val loginFlow: LoginFlow by inject()

    private fun genReq(
        username: String = "username123",
        email: String = "email",
        firstname: String = "firstname",
        lastname: String = "lastname",
        password: String = "password"
    ) = CreateAuthorRequest(
        username = username, email = email, firstname = firstname, lastname = lastname, password = password
    )

    init {
        beforeTest {
        }

        xgiven("deleteAccount") {
//            then("existing author's id") {
//                rollback {
//                    val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
//                    loginFlow.login(AuthUtilities.loginByUsernameRequest).isSuccess shouldBe true
//                    authorProfileRelatedRepository.deleteAccount(authorId) shouldBe true
//                    loginFlow.login(AuthUtilities.loginByUsernameRequest).isSuccess shouldBe false
//                }
//            }
//            then("non existing author's id") {
//                rollback {
//                    val authorId = 92345566
//                    authorRepository.getById(authorId) shouldBe null
//                    loginFlow.login(AuthUtilities.loginByUsernameRequest).isSuccess shouldBe false
//                    authorProfileRelatedRepository.deleteAccount(authorId) shouldBe false
//                }
//            }
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
    }
}
