package repositories

import models.Authors
import models.auth.Password
import models.auth.Passwords
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

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