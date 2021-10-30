package dtos

import io.ktor.http.*

//open class DtoResult<T : Enum<T>> : IDtoResult<T>() {
////    override var error: Enum<T>? = null
////    override var message: String? = null
////    override var success: Boolean? = null
//}

open class DtoResult<T : Enum<T>> {
    var statusCode: HttpStatusCode? = null
    var error: Enum<T>? = null
    var message: String? = null
    var success: Boolean? = null
}

//open class IDtoResult<T : Enum<T>> {
//    var error: Enum<T>? = null
//    var message: String? = null
//    var success: Boolean? = null
//}

//interface IDtoResult<T : Enum<T>> {
//    var error: Enum<T>?
//    var message: String?
//    var success: Boolean?
//}