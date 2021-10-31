package repositories

import repositories.interfaces.IEmailVerifyCodeRepository

class EmailVerifyCodeRepository : IEmailVerifyCodeRepository {
    override fun get(authorId: Int): String? {
        TODO("Not yet implemented")
    }

    override fun insert(code: String) {
        TODO()
    }
}