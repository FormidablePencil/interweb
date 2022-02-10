package com.idealIntent.dtos

import dtos.IApiResponseEnum
import io.ktor.http.*

// region ApiResponse

open class ApiResponse<Enum, ClassExtendedFrom>(
    override val enum: IApiResponseEnum<Enum>
) : BaseApiResponse<Enum, ClassExtendedFrom>(enum)

fun <Enum, ClassExtendedFrom> ApiResponse<Enum, ClassExtendedFrom>.succeeded(
    httpStatusCode: HttpStatusCode
): ClassExtendedFrom {
    this.isSuccess = true
    this.successHttpStatusCode = httpStatusCode
    return this as ClassExtendedFrom
}

// endregion

// region ApiDataResponse

open class ApiDataResponse<Data, Enum, ClassExtendedFrom>(
    override val enum: IApiResponseEnum<Enum>
) :
    BaseApiResponse<Enum, ClassExtendedFrom>(enum) {
    var data: Data? = null
}

fun <Data, Enum, ClassExtendedFrom> ApiDataResponse<Data, Enum, ClassExtendedFrom>.succeeded(
    httpStatusCode: HttpStatusCode,
    data: Data? = null
): ClassExtendedFrom {
    this.isSuccess = true
    this.successHttpStatusCode = httpStatusCode
    this.data = data
    return this as ClassExtendedFrom
}

// endregion

abstract class BaseApiResponse<Enum, ClassExtendedFrom>(
    open val enum: IApiResponseEnum<Enum>
) {
    var failedResponseData: Any? = null
    var isSuccess: Boolean? = null
    var successHttpStatusCode: HttpStatusCode? = null

    var code: Enum? = null

    fun message(): String? {
        return if (code == null) null
        else enum.getClientMsg(code!!)
    }

    fun statusCode(): HttpStatusCode? {
        return if (successHttpStatusCode !== null) successHttpStatusCode
        else if (code == null) null
        else enum.getHttpCode(code!!)
    }
}

fun <Enum, ClassExtendedFrom> BaseApiResponse<Enum, ClassExtendedFrom>.failed(
    code: Enum, failedResponseData: Any? = null
): ClassExtendedFrom {
    this.isSuccess = false
    this.code = code
    this.failedResponseData = failedResponseData
    return this as ClassExtendedFrom
}