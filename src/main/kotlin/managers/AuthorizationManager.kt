package managers

import configurations.IAppEnv
import helper.PassEncrypt
import models.Author
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import repositories.IAuthorRepository
import repositories.IPasswordRepository

class AuthorizationManager(
    private val authorRepository: IAuthorRepository,
    private val passwordRepository: IPasswordRepository,
    private val passEncrypt: PassEncrypt,
) : IAuthorizationManager {
    private val appEnv: IAppEnv by inject()

    override fun validateCredentials(email: String, password: String): Author {
        val author: Author? = authorRepository.getByEmail(email)
        if (author?.id == null)
            throw Exception("No user by that email of $email found")

        val passwordRecord = passwordRepository.getPassword(author.id)

        val passwordHash = passwordRecord?.password;

        if (!BCrypt.checkpw(passwordHash, password))
            throw Exception("incorrect password")

        return author
    }

    override fun setNewPassword(password: String): Int {
        val passwordId = passwordRepository.insertPassword(passEncrypt.encryptPassword(password))

        if (passwordId !is Int)
            throw Exception("Server error. Saving password failed")

        return passwordId
    }
}