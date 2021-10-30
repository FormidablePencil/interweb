package repositories.interfaces

interface IEmailVerifyCodeRepository {
    fun get(authorId: Int): String?
    fun insert(code: String)
}