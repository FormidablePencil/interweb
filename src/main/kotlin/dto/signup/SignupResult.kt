package dto.signup

import dto.*

class SignupResult(val authorId: Int) : ApiRequestResult<SignupResultError>() {
}

enum class SignupResultError {
    ServerError
}

//fun testExc() {
//    var w: SignupResult = SignupResult(231).succeeded()
//    var r: SignupResult = SignupResult(987).failed(SignupResultError.ServerError, "ErrorMessage")
//}
