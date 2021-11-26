package repositories

import io.kotest.matchers.shouldBe
import org.koin.test.inject
import repositories.interfaces.IAuthorRepository
import serialized.CreateAuthorRequest
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.SqlConstraint
import shared.testUtils.rollback

class AuthorRepositoryTest : BehaviorSpecUtRepo({
    val authorRepository: IAuthorRepository by inject()

    fun genReq(
        username: String = "username",
        email: String = "simpleEmail",
        firstname: String = "firstname",
        lastname: String = "lastname",
        password: String = "password"
    ) = CreateAuthorRequest(
        username = username, email = email, firstname = firstname, lastname = lastname, password = password
    )

    given("insertAuthor()") {
        then("insert author") {
            rollback {
                val request = genReq(
                    username = "Wallaby81",
                    email = "wallabytest123456789876543210@gmail.com81",
                )

                val authorId = authorRepository.insertAuthor(request) ?: throw Exception("did not return id")

                and("getByEmail()") {
                    val authorGotByEmail = authorRepository.getByEmail(request.email) ?: throw Exception("test failed")
                    authorGotByEmail.username shouldBe request.username
                    authorGotByEmail.email shouldBe request.email
                    authorGotByEmail.firstname shouldBe request.firstname
                    authorGotByEmail.lastname shouldBe request.lastname
                }

                and("getIdByUsername()") {
                    authorRepository.getIdByUsername(request.username) ?: throw Exception("test failed")
                }

                and("getById()") {
                    authorRepository.getById(authorId) ?: throw Exception("test failed")
                }
            }
        }

        given("constraints") {
            whenConstraint(SqlConstraint.Unique, "username", null) {
                authorRepository.insertAuthor(genReq(username = "username", email = "someEmail"))
            } // todo - create validation with valikator

            whenConstraint(SqlConstraint.Unique, "simpleEmail", null) {
                authorRepository.insertAuthor(genReq(email = "simpleEmail", username = "someUsername"))
            }

            whenConstraint(SqlConstraint.MaxSize, "simpleEmail", 100) { x ->
                authorRepository.insertAuthor(genReq(email = x))
            }

            whenConstraint(SqlConstraint.MaxSize, "username", 15) { x ->
                authorRepository.insertAuthor(genReq(username = x))
            }

            // failing and passing attempt with repository against sql db

            // failing and passing against http Request model/dto - they use valikator

            // this way no data going in from http doesn't succeed and neither one of use devs don't accidentally attempt to insert the wrong stuff
            //  - at least the console will tell you that you fucked up when creating integration tests for a functionality.
        }

        // todo - unit test sql's column type constraints
    }
})