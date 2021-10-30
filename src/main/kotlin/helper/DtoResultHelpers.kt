package helper

import dtos.ApiResponse
import io.ktor.http.*


// region Generics
// The ordering in the slots don't matter.
// The slots are where we declare the enums, where the enums are used as where the actual definition are coming from.
// Here <T> is the enum of IDtoResult and C we made it to be the class the function extends
// I'm not 100% sure why that is, however, I got it to work. There's some voodoo magic going on under the hood
// endregion


// adding a response statusCode would be nice and directly returning this object from the controller
// maybe create another extension function that returns

fun <C, T : Enum<T>> ApiResponse<T>.failed(error: Enum<T>, msg: String?, code: HttpStatusCode): C {
    this.statusCode = code
    this.error = error
    this.message = msg
    this.success = false
    return this as C
}

fun <C, T : Enum<T>> ApiResponse<T>.failed(error: Enum<T>, msg: String?): C {
    this.error = error
    this.message = msg
    this.success = false
    return this as C
}

fun <T : Enum<T>, C> ApiResponse<T>.failed(error: Enum<T>, code: HttpStatusCode): C {
    this.statusCode = code
    this.error = error
    this.success = false
    return this as C
}

fun <T : Enum<T>, C> ApiResponse<T>.failed(error: Enum<T>): C {
    this.error = error
    this.success = false
    return this as C
}

fun <C, T : Enum<T>> ApiResponse<T>.succeeded(code: HttpStatusCode): C {
    this.statusCode = code
    this.success = true
    return this as C
}

fun <C, T : Enum<T>> ApiResponse<T>.succeeded(): C {
    this.success = true
    return this as C
}
