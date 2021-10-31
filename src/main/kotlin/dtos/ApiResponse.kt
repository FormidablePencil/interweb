package dtos

import io.ktor.http.*

interface IApiResponseEnum<Code> {
    fun message(code: Code): String
    fun statusCode(code: Code): HttpStatusCode
}

open class ApiResponse<Data, Code>(val enum: IApiResponseEnum<Code>) {
    var data: Data? = null
    var success: Boolean? = null
    var successHttpStatusCode: HttpStatusCode? = null

    var code: Code? = null

    fun data(): Data {
        return data!!
    }

    fun success(): Boolean {
        return success != null
    }

    fun message(): String? {
        return if (code == null) null
        else enum.message(code!!)
    }

    fun statusCode(): HttpStatusCode? {
        return if (successHttpStatusCode !== null) successHttpStatusCode
        else if (code == null) null
        else enum.statusCode(code!!)
    }
}

fun <C, Data, Code> ApiResponse<Data, Code>.succeeded(httpStatusCode: HttpStatusCode): C {
    this.successHttpStatusCode = httpStatusCode
    this.success = true
    return this as C
}

fun <C, Data, Code> ApiResponse<Data, Code>.succeeded(httpStatusCode: HttpStatusCode, data: Data): C {
    this.data = data
    this.successHttpStatusCode = httpStatusCode
    this.success = true
    return this as C
}

fun <C, Data, Code> ApiResponse<Data, Code>.failed(code: Code): C {
    this.code = code
    this.success = false
    return this as C
}

