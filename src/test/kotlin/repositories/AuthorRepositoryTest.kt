package repositories

import dtos.author.CreateAuthorRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.koin.test.get
import repositories.interfaces.IAuthorRepository
import shared.BehaviorSpecUtRepo
import shared.rollbackGiven

class AuthorRepositoryTest : BehaviorSpecUtRepo({
    val authorRepository: IAuthorRepository = get()

    rollbackGiven("insertAuthor()") {

        val request = CreateAuthorRequest(
            username = "Wallaby81",
            email = "wallabytest123456789876543210@gmail.com81",
            firstname = "Bee",
            lastname = "Wall",
            password = "Wallaby4Days!",
        )

        val authorId = authorRepository.insertAuthor(request)
            ?: throw Exception("did not return id")

        val authorGotByEmail = authorRepository.getByEmail(request.email)
        val authorGotByUsername = authorRepository.getByUsername(request.username)
        val authorGotById = authorRepository.getById(authorId)

//        And("insertAuthor() again should failed") {
//            When("username is the same") {
//                val req = CreateAuthorRequest(
//                    request.username, request.email, request.firstname, request.lastname, request.password
//                )
//                authorRepository.insertAuthor(req) shouldBe null
//            }
//
//            When("email is the same") {
//                val req = CreateAuthorRequest(
//                    request.username, request.email, request.firstname, request.lastname, request.password
//                )
//                authorRepository.insertAuthor(req) shouldBe null
//            }
//        }

        then("getByEmail()") {
            authorGotByEmail ?: throw Exception("test failed")
            authorGotByEmail.username shouldBe request.username
            authorGotByEmail.email shouldBe request.email
            authorGotByEmail.firstname shouldBe request.firstname
            authorGotByEmail.lastname shouldBe request.lastname
        }

        then("getByUsername()") {
            authorGotByUsername ?: throw Exception("test failed")
        }

        then("getById()") {
            authorGotById ?: throw Exception("test failed")
        }
    }
})