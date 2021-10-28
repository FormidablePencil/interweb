package unitTests.managers.token

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import managers.TokenManager
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.ITokenRepository
import shared.BehaviorSpecUT

class MyTests : FunSpec({
    test("String length should return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})

class GenerateTokensUT : BehaviorSpecUT({
    var authorRepository: IAuthorRepository = mockk()
    var tokenRepository: ITokenRepository = mockk()
    val authorId = 321
    val username = "formidable"

    every { tokenRepository.deleteOldTokens(username, authorId) } returns Unit
    every { tokenRepository.insertTokens(any(), any(), authorId) } returns 1

    var tokenManager: TokenManager = TokenManager(authorRepository, tokenRepository)

    Given("authorId and username") {
        Then("return new valid token") {
            val result = tokenManager.generateTokens(authorId, username)

            result.success shouldBe true

            result.refreshToken.size shouldBe 1
            result.accessToken.size shouldBe 1

//                TODO() // decode tokens and check that expiration time, authorId and username is correct
        }
    }
})