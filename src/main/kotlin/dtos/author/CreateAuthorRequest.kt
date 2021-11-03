package dtos.author

import kotlinx.serialization.Serializable
import org.valiktor.Validator
import org.valiktor.constraints.NotBlank
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEqualToIgnoringCase
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

/**
 * Create author request
 *
 * @property username
 * @property email
 * @property firstname
 * @property lastname
 * @property password
 * @constructor Create empty Create author request
 */
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
            validate(CreateAuthorRequest::email).validateEmail()
            validate(CreateAuthorRequest::username).validateBasic()
            validate(CreateAuthorRequest::firstname).validateBasic()
            validate(CreateAuthorRequest::password).validatePassword()
        }
    }
}

/**
 * Main
 *
 */
fun main() {
    val r = CreateAuthorRequest("username", "email", "firstname", "lastname", "password")
}

/**
 * Validate basic
 *
 * @param E
 */
fun <E> Validator<E>.Property<String?>.validateBasic() {
    this.isNotBlank()
}

/**
 * Validate email
 *
 * @param E
 */
fun <E> Validator<E>.Property<String?>.validateEmail() {
    this.isNotBlank()
    this.hasSize(min = 2, max = 20)
}

/**
 * Validate password
 *
 * @param E
 */
fun <E> Validator<E>.Property<String?>.validatePassword() {
    this.hasSize(min = 3, max = 80)
}