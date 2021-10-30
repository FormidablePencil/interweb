package dtos

open class DtoResult<T> {
    var failedCode: T? = null
    var success: Boolean = false
}

fun <T, C> DtoResult<T>.failed(error: T): C {
    this.failedCode = error
    this.success = false
    return this as C
}


fun <C, T : Enum<T>> DtoResult<T>.succeeded(): C {
    this.success = true
    return this as C
}
