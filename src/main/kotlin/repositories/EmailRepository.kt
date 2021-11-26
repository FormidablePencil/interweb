package repositories

class EmailRepository {
    fun get(authorId: Int): String? {
        TODO("Not yet implemented")
    }

    suspend fun insertResetPasswordCode(code: String, authorId: Int) {
        TODO("Not yet implemented")
    }

    suspend fun insertEmailVerificationCode(code: String, authorId: Int) {
        TODO("Not yet implemented")
    }
}