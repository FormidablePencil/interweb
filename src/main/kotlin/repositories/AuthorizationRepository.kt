package repositories

import models.Author
import models.Authors
import models.auth.Passwords
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.mindrot.jbcrypt.BCrypt

class AuthorizationRepository: RepositoryBase(), IAuthorizationRepository {
    override val Database.author get() = this.sequenceOf(Authors)
    override val Database.password get() = this.sequenceOf(Passwords)

    override fun validateCredentials(email: String, password: String): Author {
        var author = database.author.find() { it.email eq email }
        if (author?.id == null)
            throw Exception("No user by that email of $email found")

        var passwordRecord = database.password.find() { it.id eq author.id }
        var passwordHash = passwordRecord?.password;

        if (!BCrypt.checkpw(passwordHash, password))
            throw Exception("incorrect password")

        return author
    }

    override fun setPassword(password: String): Int {
        var passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

        var passwordId = database.insertAndGenerateKey(Passwords) {
            set(it.password, passwordHash)
        }

        if (passwordId !is Int)
            throw Exception("Server error. Saving password failed")

        return passwordId
    }
}