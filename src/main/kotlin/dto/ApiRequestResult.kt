package dto

abstract class ApiRequestResult<Value, ErrorCode> : BaseApiRequestResult<ErrorCode>() {
    var value: Value? = null

    fun success(value: Value) {
        this.value = value
        success = true
    }
}

open class BaseApiRequestResult<ErrorCode> {
    var message: String? = null
    var success = false

    fun errorCode(message: String) {
        this.message = message
        success = false
    }

    fun errorCode(errorCode: ErrorCode, message: String) {
        this.message = message
    }

    fun success() {
        success = true
    }
}