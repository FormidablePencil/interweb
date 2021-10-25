package domainServices

import dto.token.LoginResult
import dto.token.TokensResult
import managers.ITokenManager
import repositories.AuthorizationRepository
import repositories.IAuthorRepository
import repositories.IAuthorizationRepository
import java.util.HashMap

class LoginDomainService(
    private val tokenManager: ITokenManager,
    private val authorizationRepository: IAuthorizationRepository,
) {
    // -----------
    // we need refresh access token working
    // we need login to return tokens

    // once we have the tokens working, then authorizing restricted data can be done
    // once that is done, we'll have a good structure with
    // codebase, unit tests and integration tests.
    // We're aiming to rig the codebase with unit and integration tests baby
    // Once these things are done, we can develop features and make my dreams come true
    // -----------

    // -- game plan --
    // unit tests all the code for those 2 things - mock
    // make integration tests execute routes and not domainServices as we have at the moment
    // ---------------

    // tokens must be saved in db
    // tokens must be sent from the client via bearer
    // and validated everytime before giving access to data

    // access token will have authorId in it so that we how to get identical token from db
    // and if data is requested, we can retrieve users data by authorId

    fun login(email: String, password: String): LoginResult {
        // validateCredentials and get author row if successful
        // get tokens
        // return author data and tokens back to client
        var author = authorizationRepository.validateCredentials(email, password)

        var tokensResult = tokenManager.generateTokens(author.id, author.username)

        return LoginResult(author.id, tokensResult)
    }
}