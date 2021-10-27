package dto

open class ApiRequestResult<T : Enum<T>> : BaseApiRequestResultExtFun<T> {
    override var error: Enum<T>? = null
    override var message: String? = null
    override var success: Boolean? = null
}

interface BaseApiRequestResultExtFun<T: Enum<T>> {
    var error: Enum<T>?
    var message: String?
    var success: Boolean?
}

fun <C, T : Enum<T>> BaseApiRequestResultExtFun<T>.failed(error: Enum<T>, msg: String?): C {
    this.error = error
    this.message = msg
    this.success = false
    return this as C
}

fun <C, T : Enum<T>> BaseApiRequestResultExtFun<T>.succeeded(): C {
    this.success = true
    return this as C
}
