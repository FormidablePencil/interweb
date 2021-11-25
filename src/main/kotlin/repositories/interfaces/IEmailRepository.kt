package repositories.interfaces

interface IEmailRepository {
    fun get(authorId: Int): String?
    fun insertResetPasswordCode(code: String, authorId: Int)
    fun insertEmailVerificationCode(code: String, authorId: Int)
}