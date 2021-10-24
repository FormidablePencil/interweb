package dto.author

data class CreateAuthorRequest(
    val username: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String,
) {
    var encryptedPassword: String? = null
}