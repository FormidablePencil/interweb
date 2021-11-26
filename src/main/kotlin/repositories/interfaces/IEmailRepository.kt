package repositories.interfaces

interface IEmailRepository {
    fun get(authorId: Int): String?
    suspend fun insertResetPasswordCode(code: String, authorId: Int)
    suspend fun insertEmailVerificationCode(code: String, authorId: Int)
}