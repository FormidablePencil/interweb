package dtos

import io.ktor.http.*

// region Explanation

// endregion

// region ApiResponse

open class ApiResponse<Enum, ClassExtendedFrom>(
    override val enum: IApiResponseEnum<Enum>
) : BaseApiResponse<Enum, ClassExtendedFrom>(enum)

fun <Enum, ClassExtendedFrom> ApiResponse<Enum, ClassExtendedFrom>.succeeded(
    httpStatusCode: HttpStatusCode
): ClassExtendedFrom {
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
    data: Data
): ClassExtendedFrom {
    this.successHttpStatusCode = httpStatusCode
    this.data = data
    return this as ClassExtendedFrom
}

// endregion

interface IApiResponseEnum<Enum> {
    fun getMsg(code: Enum): String
    fun getStatusCode(code: Enum): HttpStatusCode
}

abstract class BaseApiResponse<Enum, ClassExtendedFrom>(
    open val enum: IApiResponseEnum<Enum>
) {
    var successHttpStatusCode: HttpStatusCode? = null

    var code: Enum? = null

    fun message(): String {
        return if (code == null) ""
        else enum.getMsg(code!!)
    }

    fun statusCode(): HttpStatusCode? {
        return if (successHttpStatusCode !== null) successHttpStatusCode
        else if (code == null) null
        else enum.getStatusCode(code!!)
    }
}

fun <Enum, ClassExtendedFrom> BaseApiResponse<Enum, ClassExtendedFrom>.failed(
    code: Enum
): ClassExtendedFrom {
    this.code = code
    return this as ClassExtendedFrom
}