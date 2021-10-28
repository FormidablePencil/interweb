package dtos

open class DtoResult<T : Enum<T>> : IDtoResult<T> {
    override var error: Enum<T>? = null
    override var message: String? = null
    override var success: Boolean? = null
}

interface IDtoResult<T : Enum<T>> {
    var error: Enum<T>?
    var message: String?
    var success: Boolean?
}