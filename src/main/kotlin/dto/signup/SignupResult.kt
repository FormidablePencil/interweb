package dto.signup

import dto.*

data class SignupResult(var authorId: Int? = null) : Result<SignupResultError>()

enum class SignupResultError {
    ServerError, WeakPassword, InvalidEmailFormat
}

//fun testExc() {
//    var w: SignupResult = SignupResult(231).succeeded()
//    var r: SignupResult = SignupResult(987).failed(SignupResultError.ServerError, "ErrorMessage")
//    println(w)
//}
