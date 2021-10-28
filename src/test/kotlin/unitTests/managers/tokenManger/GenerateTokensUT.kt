package unitTests.managers.tokenManger

import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import managers.TokenManager
import repositories.IAuthorRepository
import repositories.ITokenRepository
import shared.UtBehaviorSpec

class GenerateTokensUT : UtBehaviorSpec() {
    var authorRepository: IAuthorRepository = mockk()
    var tokenRepository: ITokenRepository = mockk()
    lateinit var tokenManager: TokenManager
    val authorId = 321
    val username = "formidable"

    init {
        every { tokenRepository.deleteOldTokens(username, authorId) } returns Unit
        every { tokenRepository.insertTokens(any(), any(), authorId) } returns 1

        tokenManager = TokenManager(authorRepository, tokenRepository)

        Given("authorId and username") {
            Then("return new valid token") {
                val result = tokenManager.generateTokens(authorId, username)

                result.success shouldBe true

                result.refreshToken.size shouldBeGreaterThan 0
                result.accessToken.size shouldBeGreaterThan 0

//                TODO() // decode tokens and check that expiration time, authorId and username is correct
            }
        }
    }
}