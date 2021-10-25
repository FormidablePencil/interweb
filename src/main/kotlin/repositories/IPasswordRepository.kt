package repositories

import models.Author
import models.Authors
import models.auth.Password
import models.auth.Passwords
import org.ktorm.database.Database
import org.ktorm.entity.EntitySequence

interface IPasswordRepository {
    val Database.author: EntitySequence<Author, Authors>
    val Database.password: EntitySequence<Password, Passwords>
    fun getPassword(authorId: Int): Password?
    fun insertPassword(passwordHash: String): Int?
}