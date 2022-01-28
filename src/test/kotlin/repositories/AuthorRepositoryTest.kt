package repositories

import org.koin.test.inject
import repositories.profile.AuthorProfileRelatedRepository
import repositories.profile.AuthorRepository
import serialized.CreateAuthorRequest
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class AuthorRepositoryTest : BehaviorSpecUtRepo({
    val authorRepository: AuthorRepository by inject()
    val authorProfileRelatedRepository: AuthorProfileRelatedRepository by inject()

    fun genReq(
        username: String = "username",
        email: String = "email",
        firstname: String = "firstname",
        lastname: String = "lastname",
        password: String = "password"
    ) = CreateAuthorRequest(
        username = username, email = email, firstname = firstname, lastname = lastname, password = password
    )

    given("created new account.") {
        then("getByUsername(), getAssortmentById()") {
            rollback {
                val request = genReq(
                    username = "Wallaby81",
                    email = "wallabytest123456789876543210@gmail.com81",
                )

                val authorId =
                    authorProfileRelatedRepository.createNewAuthor(request) ?: throw Exception("did not return id")

                // todo - fix
//                and("getByEmail()") {
//                    val authorGotByEmail = authorRepository.getByEmail(request.email) ?: throw Exception("test failed")
//                    authorGotByEmail.username shouldBe request.username
//                    authorGotByEmail.email shouldBe request.email
//                    authorGotByEmail.firstname shouldBe request.firstname
//                    authorGotByEmail.lastname shouldBe request.lastname
//                }

                authorRepository.getByUsername(request.username) ?: throw Exception("test failed")
                authorRepository.getById(authorId) ?: throw Exception("test failed")
            }
        }
    }
})