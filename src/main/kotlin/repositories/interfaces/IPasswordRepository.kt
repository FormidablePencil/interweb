package repositories.interfaces

import models.profile.Author
import models.profile.Authors
import models.authorization.Password
import models.authorization.Passwords
import org.ktorm.database.Database
import org.ktorm.entity.EntitySequence

interface IPasswordRepository {
    val Database.author: EntitySequence<Author, Authors>
    val Database.password: EntitySequence<Password, Passwords>
    fun getPassword(authorId: Int): Password?
    fun insertPassword(passwordHash: String): Int?
}