package domainServices

import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import managers.ITokenManager
import repositories.IAuthorRepository

class SignupDomainService(
    val authorRepository: IAuthorRepository,
    val tokenManager: ITokenManager
) {
    fun Signup(request: CreateAuthorRequest): SignupResult {
        // save encrypted password, create author column and generate jwt tokens
        // return jwt tokens and authorId

        request.encryptedPassword = request.password;

        var createAuthorResult = authorRepository.createAuthor(request)

//        tokenManager.authenticate(email, username)

//        var result = authorRepository.GetByUsername()
        val authorId = 1

        return SignupResult(authorId)
    }
}