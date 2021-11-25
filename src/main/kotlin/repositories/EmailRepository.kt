package repositories

import repositories.interfaces.IEmailRepository

class EmailRepository : IEmailRepository {
    override fun get(authorId: Int): String? {
        TODO("Not yet implemented")
    }

    override fun insertResetPasswordCode(code: String, authorId: Int) {
        TODO("Not yet implemented")
    }

    override fun insertEmailVerificationCode(code: String, authorId: Int) {
        TODO("Not yet implemented")
    }
}