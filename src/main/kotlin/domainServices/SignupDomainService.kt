package domainServices

import repositories.AuthorRepository

class SignupDomainService(
    val authorRepository: AuthorRepository
) {
    fun Signup(email: String, username: String, password: String): Int {
        authorRepository.CreateAuthor("username")

        val authorId = 1
        return authorId
    }
}