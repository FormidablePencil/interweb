package repositories

import models.authorization.Password
import models.authorization.Passwords
import models.profile.Authors
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.interfaces.IPasswordRepository

class PasswordRepository : RepositoryBase(), IPasswordRepository {
    private val Database.author get() = this.sequenceOf(Authors)
    private val Database.password get() = this.sequenceOf(Passwords)

    override fun getPassword(authorId: Int): Password? {
        return database.password.find() { it.authorId eq authorId }
    }

    override fun insertPassword(passwordHash: String): Int {
        return database.insertAndGenerateKey(Passwords) {
            set(it.password, passwordHash)
        } as Int
    }

    override fun deletePassword(authorId: Int): Int {
        return database.delete(Passwords) { it.authorId eq authorId }
    }
}