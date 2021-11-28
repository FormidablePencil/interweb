package repositories

import models.profile.Author
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import repositories.profile.AuthorRepository

class AuthorViewerRepository : AuthorRepository() {
}