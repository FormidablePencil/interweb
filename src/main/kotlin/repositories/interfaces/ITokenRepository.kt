package repositories.interfaces

interface ITokenRepository {
    fun insertTokens(
        refreshToken: HashMap<String, String>,
        accessToken: HashMap<String, String>,
        authorId: Int
    ): Int
    fun deleteOldTokens(username: String, authorId: Int)
}