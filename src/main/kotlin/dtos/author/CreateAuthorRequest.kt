package dtos.author

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

@Serializable
data class CreateAuthorRequest(
    val username: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String,
) {
    init {
        validate(this) {
            validate(CreateAuthorRequest::username).isNotBlank()
            validate(CreateAuthorRequest::firstname).isNotBlank()
            validate(CreateAuthorRequest::password).hasSize(min = 3, max = 80)
        }
    }
}
