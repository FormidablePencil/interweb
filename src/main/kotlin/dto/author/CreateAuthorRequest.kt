package dto.author

data class CreateAuthorRequest(
    val username: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val passwordId: Int,
) {
    var encryptedPassword: String? = null
}