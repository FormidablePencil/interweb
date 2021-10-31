package dtos.authorization

import dtos.ApiDataResponse
import dtos.ApiResponse
import dtos.IApiResponseEnum
import io.ktor.http.*

data class ResetPasswordResponse(
    val refreshToken: String?, val accessToken: String?
) : ApiResponse<ResetPasswordResponseFailed, ResetPasswordResponse>(ResetPasswordResponseFailed)

enum class ResetPasswordResponseFailed {
    f;

    companion object : IApiResponseEnum<ResetPasswordResponseFailed> {
        override fun getMsg(code: ResetPasswordResponseFailed): String {
            TODO("Not yet implemented")
        }

        override fun getStatusCode(code: ResetPasswordResponseFailed): HttpStatusCode {
            TODO("Not yet implemented")
        }

    }
}
