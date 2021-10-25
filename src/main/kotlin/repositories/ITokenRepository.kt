package repositories

interface ITokenRepository {
    fun insertTokens(
        refreshToken: HashMap<String, String>,
        accessToken: HashMap<String, String>,
        authorId: Int
    ): Int
}