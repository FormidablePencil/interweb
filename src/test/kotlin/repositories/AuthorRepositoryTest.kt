package repositories

import dtos.author.CreateAuthorRequest
import io.kotest.matchers.shouldBe
import org.koin.test.get
import repositories.interfaces.IAuthorRepository
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollbackGiven

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

        val authorId = authorRepository.insertAuthor(request) ?: throw Exception("did not return id")

        then("getByEmail()") {
            val authorGotByEmail = authorRepository.getByEmail(request.email) ?: throw Exception("test failed")
            authorGotByEmail.username shouldBe request.username
            authorGotByEmail.email shouldBe request.email
            authorGotByEmail.firstname shouldBe request.firstname
            authorGotByEmail.lastname shouldBe request.lastname
        }

        then("getByUsername()") {
            authorRepository.getByUsername(request.username) ?: throw Exception("test failed")
        }

        then("getById()") {
            authorRepository.getById(authorId) ?: throw Exception("test failed")
        }
    }


    given("insertPassword() with already existing author id") {
        val request = CreateAuthorRequest(
            username = "existing username",
            email = "existing email",
            firstname = "existing",
            lastname = "existing",
            password = "existing",
        )



    }
})