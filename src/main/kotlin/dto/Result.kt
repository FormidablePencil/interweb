package dto

open class Result<T : Enum<T>> : IResult<T> {
    override var error: Enum<T>? = null
    override var message: String? = null
    override var success: Boolean? = null
}

interface IResult<T: Enum<T>> {
    var error: Enum<T>?
    var message: String?
    var success: Boolean?
}

// region Generics
// The ordering in the slots don't matter.
// The slots are where we declare the enums, where the enums are used as where the actual definition are coming from.
// Here <T> is the enum of IResult and C we made it to be the class the function extends
// I'm not 100% sure why that is, however, I got it to work. There's some voodoo magic going on under the hood
// endregion

fun <T : Enum<T>, C> IResult<T>.failed(error: Enum<T>): C {
    this.error = error
    this.success = false
    return this as C
}

fun <T : Enum<T>, C> IResult<T>.failed(error: Enum<T>, msg: String?): C {
    this.error = error
    this.message = msg
    this.success = false
    return this as C
}

fun <T : Enum<T>, C> IResult<T>.succeeded(): C {
    this.success = true
    return this as C
}
