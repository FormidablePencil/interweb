package managers

interface ITokenManager {
    fun authenticate(email: String, password: String)
    fun getNewAccessToken(refreshToken: String)
}