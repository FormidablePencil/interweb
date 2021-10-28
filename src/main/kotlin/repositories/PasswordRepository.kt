package repositories

import models.profile.Authors
import models.authorization.Password
import models.authorization.Passwords
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.interfaces.IPasswordRepository

class PasswordRepository : RepositoryBase(), IPasswordRepository {
    override val Database.author get() = this.sequenceOf(Authors)
    override val Database.password get() = this.sequenceOf(Passwords)

    override fun getPassword(authorId: Int): Password? {
        return database.password.find() { it.authorId eq authorId }
    }

    override fun insertPassword(passwordHash: String): Int? {
        return database.insertAndGenerateKey(Passwords) {
            set(it.password, passwordHash)
        } as Int?
    }
}